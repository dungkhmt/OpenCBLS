/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * And open the template in the editor.
 */
package localsearch.application;

import localsearch.constraint.basic.logic.And;
import localsearch.constraint.basic.logic.Implicate;
import localsearch.constraint.basic.logic.Or;
import localsearch.constraint.basic.operator.Equal;
import localsearch.constraint.basic.operator.LessEqual;
import localsearch.function.math.FuncPlus;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.TabuSearch;
import localsearch.solver.lns_solver.LnsSolver;
import localsearch.solver.lns_solver.implementation.ConstraintObjective;
import localsearch.utils.NumberUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author HienHoang (05-2018)
 */
public class MultiBinpacking2D {

    static class Bin {

        List<Packet> packets;
        int width;
        int height;

        public Bin(List<Packet> packets, int width, int height) {
            this.packets = packets;
            this.width = width;
            this.height = height;
        }

    }

    static class Packet {

        Integer w;
        Integer h;
        VarIntLS x;
        VarIntLS y;
        VarIntLS r;
        VarIntLS b;

        /**
         * New packet
         *
         * @param w width
         * @param h height
         * @param x x_position
         * @param y y_position
         * @param r rotate
         * @param b bin id
         */
        public Packet(int w, int h, VarIntLS x, VarIntLS y, VarIntLS r, VarIntLS b) {
            this.w = w;
            this.h = h;
            this.x = x;
            this.y = y;
            this.r = r;
            this.b = b;
        }

    }

    private final List<Bin> bins;
    private List<Packet> packets;
    private ConstraintSystem cs;
    private int numBinsUsed;
    private Random rd;
    private LocalSearchManager localSearchManager;

    public MultiBinpacking2D(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = br.readLine();
        String[] ps = line.split(" ");
        int numBins = Integer.parseInt(ps[0]);
        int numPackets = Integer.parseInt(ps[1]);
        bins = new ArrayList<>();
        packets = new ArrayList<>();

        for (int i = 0; i < numBins; ++i) {
            line = br.readLine();
            ps = line.split(" ");
            bins.add(new Bin(new ArrayList<Packet>(), Integer.parseInt(ps[0]),
                    Integer.parseInt(ps[1])));
        }
        for (int i = 0; i < numPackets; ++i) {
            line = br.readLine();
            ps = line.split(" ");
            packets.add(new Packet(Integer.parseInt(ps[0]),
                    Integer.parseInt(ps[1]), null, null, null, null));
        }

        bins.sort((o1, o2) -> Integer.compare(o2.height * o2.width, o1.height * o1.width));

        br.close();
    }

    private void stateModel(int maxBins, List<Packet> oldPackets) {
        localSearchManager = new LocalSearchManager();
        cs = new ConstraintSystem();
        this.numBinsUsed = maxBins;
        rd = new Random(1L);

        int max = -1;
        for (Bin bin : bins) {
            max = Math.max(max, Math.max(bin.height, bin.width));
        }

        for (Packet packet : packets) {
            packet.x = new VarIntLS(0, max - 1, localSearchManager, rd);
            packet.y = new VarIntLS(0, max - 1, localSearchManager, rd);
            packet.r = new VarIntLS(0, 1, localSearchManager, rd);
            packet.b = new VarIntLS(0, maxBins - 1, localSearchManager, rd);
            for (int j = 0; j < bins.size(); ++j) {
                IConstraint c1 = new Implicate(new Equal(packet.r, 0),
                        new And(new LessEqual(new FuncPlus(packet.y, packet.h), bins.get(j).height),
                                new LessEqual(new FuncPlus(packet.x, packet.w), bins.get(j).width)));
                IConstraint c2 = new Implicate(new Equal(packet.r, 1),
                        new And(new LessEqual(new FuncPlus(packet.y, packet.w), bins.get(j).height),
                                new LessEqual(new FuncPlus(packet.x, packet.h), bins.get(j).width)));
                cs.post(new Implicate(new Equal(packet.b, j), new And(c1, c2)));
            }
        }
        if (oldPackets != null) {
            for (int i = 0; i < packets.size(); ++i) {
                packets.get(i).x.setValue(oldPackets.get(i).x.getValue());
                packets.get(i).y.setValue(oldPackets.get(i).y.getValue());
                packets.get(i).r.setValue(oldPackets.get(i).r.getValue());
                Integer bValue = oldPackets.get(i).b.getValue();
                if (bValue < maxBins) {
                    packets.get(i).b.setValue(bValue);
                }
            }
        }

        for (int i = 0; i < packets.size(); ++i) {
            Packet pi = packets.get(i);
            for (int j = i + 1; j < packets.size(); ++j) {
                Packet pj = packets.get(j);
                IConstraint sameBin = new Equal(pi.b, pj.b);
                IConstraint[] overlaps = new IConstraint[4];
                IConstraint[] c = new IConstraint[4];
                c[0] = new LessEqual(new FuncPlus(pi.x, pi.w), pj.x);
                c[1] = new LessEqual(new FuncPlus(pi.y, pi.h), pj.y);
                c[2] = new LessEqual(new FuncPlus(pj.x, pj.w), pi.x);
                c[3] = new LessEqual(new FuncPlus(pj.y, pj.h), pi.y);
                overlaps[0] = new Implicate(new And(new Equal(pi.r, 0), new Equal(pj.r, 0)), new Or(c));

                c = new IConstraint[4];
                c[0] = new LessEqual(new FuncPlus(pi.x, pi.h), pj.x);
                c[1] = new LessEqual(new FuncPlus(pi.y, pi.w), pj.y);
                c[2] = new LessEqual(new FuncPlus(pj.x, pj.w), pi.x);
                c[3] = new LessEqual(new FuncPlus(pj.y, pj.h), pi.y);
                overlaps[1] = new Implicate(new And(new Equal(pi.r, 1), new Equal(pj.r, 0)), new Or(c));

                c = new IConstraint[4];
                c[0] = new LessEqual(new FuncPlus(pi.x, pi.w), pj.x);
                c[1] = new LessEqual(new FuncPlus(pi.y, pi.h), pj.y);
                c[2] = new LessEqual(new FuncPlus(pj.x, pj.h), pi.x);
                c[3] = new LessEqual(new FuncPlus(pj.y, pj.w), pi.y);
                overlaps[2] = new Implicate(new And(new Equal(pi.r, 0), new Equal(pj.r, 1)), new Or(c));

                c = new IConstraint[4];
                c[0] = new LessEqual(new FuncPlus(pi.x, pi.h), pj.x);
                c[1] = new LessEqual(new FuncPlus(pi.y, pi.w), pj.y);
                c[2] = new LessEqual(new FuncPlus(pj.x, pj.h), pi.x);
                c[3] = new LessEqual(new FuncPlus(pj.y, pj.w), pi.y);
                overlaps[3] = new Implicate(new And(new Equal(pi.r, 1), new Equal(pj.r, 1)), new Or(c));

                cs.post(new Implicate(sameBin, new And(overlaps)));
            }
        }
        IFunction[] binIFunctions = new IFunction[packets.size()];
        for (int i = 0; i < packets.size(); ++i) {
            binIFunctions[i] = new FuncVar(packets.get(i).b);
        }
        cs.close();
        localSearchManager.close();

        System.out.format("Num variables: %d\nNum invariants: %d\n",
                localSearchManager.getNumVariables(), localSearchManager.getNumInvariants());
    }

    private final static String[] COLOR
            = //<editor-fold defaultstate="collapsed" desc="Colors">
            new String[]{
                    "#7ED379", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
                    "#FF0000", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87",
                    "#5A0007", "#809693", "#1B4400", "#4FC601", "#3B5DFF", "#4A3B53", "#FF2F80",
                    "#61615A", "#BA0900", "#6B7900", "#00C2A0", "#FFAA92", "#FF90C9", "#B903AA", "#D16100",
                    "#FFDBE5", "#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8", "#013349", "#00846F",
                    "#372101", "#FFB500", "#C2FFED", "#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09",
                    "#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75", "#B77B68", "#7A87A1", "#788D66",
                    "#885578", "#FAD09F", "#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED", "#886F4C",
                    "#34362D", "#B4A8BD", "#00A6AA", "#452C2C", "#636375", "#A3C8C9", "#FF913F", "#938A81",
                    "#575329", "#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757", "#C8A1A1", "#1E6E00",
                    "#7900D7", "#A77500", "#6367A9", "#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700",
                    "#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF", "#99ADC0", "#3A2465", "#922329",
                    "#5B4534", "#FDE8DC", "#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72", "#6A3A4C",
                    "#83AB58", "#001C1E", "#D1F7CE", "#004B28", "#C8D0F6", "#A3A489", "#806C66", "#222800",
                    "#BF5650", "#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4", "#1E0200", "#5B4E51",
                    "#C895C5", "#320033", "#FF6832", "#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58"
            };
//</editor-fold>

    public void saveHtml(String filePath) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
        bw.write(
                "<!doctype html>\n<html>\n<head>\n<title>MultiBinpacking2D</title>\n</head>\n<body>\n\n");

        for (int i = 0; i < bins.size(); ++i) {
            bins.get(i).packets.clear();
        }

        for (int i = 0; i < packets.size(); ++i) {
            Packet p = packets.get(i);
            bins.get(p.b.getValue()).packets.add(p);
        }

        for (int i = 0; i < bins.size(); ++i) {
            if (!bins.get(i).packets.isEmpty()) {
                bw.write("Bin " + (i + 1));
                bw.write(binToHtml(bins.get(i)));
                bw.write("\n<br></br>\n");
            }
        }

        bw.write("</body></html>\n");
        bw.close();
        System.out.println("Saved solution to: " + filePath);
    }

    private String binToHtml(Bin bin) {
        StringBuilder sb = new StringBuilder();
        int n = bin.packets.size();
        boolean[] isIndex = new boolean[n + 2];
        int size = 650 / (Math.max(bin.width, bin.height) + 1);
        sb.append(
                "<style type=\"text/css\">\n" + "table, td {\n" + "\t\tborder : 1px solid black;\n" + "\t\tborder-collapse: collapse;text-align : center;\n" + "\t}\n" + "\ttd {\n" + "\t\twidth : +").
                append(size).append("px;\n" + "\t\theight: +").append(size).
                append("px;\n" + "\t}\n");
        for (int i = 0; i < n; i++) {
            sb.append("td.class").append(i).
                    append(" { \n background-color:").
                    append(COLOR[i]).append(";  ").append("color: white;\n}\n");
        }
        sb.append("</style>\n");
        sb.append("<table>\n");
        for (int i = 0; i <= bin.height; i++) {
            sb.append("<tr>\n");
            for (int j = 0; j <= bin.width; j++) {
                if (i == 0) {
                    if (j == 0) {
                        sb.append("<td>");
                    } else {
                        sb.append("<td>\n");
                        sb.append(j);
                    }
                    sb.append("</td>\n");
                } else if (j == 0) {
                    sb.append("<td>");
                    sb.append(i);
                    sb.append("</td>\n");
                } else {
                    boolean flag = false;
                    for (int k = 0; k < n; k++) {
                        Packet p = bin.packets.get(k);
                        int xk = p.x.getValue();//x[k].getValue();
                        int yk = p.y.getValue();//y[k].getValue();
                        int wk = p.w;
                        int hk = p.h;
                        //if (o[k].getValue() == 0) {
                        if (p.r.getValue() == 0) {
                            if (j - 1 >= xk && j - 1 <= xk + wk - 1 && i - 1 >= yk && i - 1 <= yk + hk - 1) {
                                sb.append("<td class='class").append(k).
                                        append("'>");

                                if (!isIndex[k] && (j - 1) == (xk + xk + wk - 1) / 2 && (i - 1) == (yk + yk + hk - 1) / 2) {
                                    sb.append(k);
                                    isIndex[k] = true;
                                }
                                flag = true;
                                break;
                            }
                        } else if (j - 1 >= xk && j - 1 <= xk + hk - 1 && i - 1 >= yk && i - 1 <= yk + wk - 1) {
                            sb.append("<td class='class").append(k).append(
                                    "'>");
                            if (!isIndex[k] && (j - 1) == (xk + xk + hk - 1) / 2 && (i - 1) == (yk + yk + wk - 1) / 2) {
                                sb.append(k);
                                isIndex[k] = true;
                            }
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        sb.append("</td>\n");
                    } else {
                        sb.append("<td>");
                        sb.append("</td>\n");
                    }
                }

            }
            sb.append("</tr>\n");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public static void genInput(String filePath, int numBins, int numPackets,
                                Random rd) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(String.format("%d %d\n", numBins, numPackets));
            int minBinWidth = 100;
            int minBinHeight = 100;
            for (int i = 0; i < numBins; ++i) {
                int binWidth = rd.nextInt(8) + 15;
                int binHeight = rd.nextInt(8) + 15;
                minBinWidth = Math.min(minBinWidth, binWidth);
                minBinHeight = Math.min(minBinHeight, binHeight);
                bw.write(String.format("%d %d\n", binWidth, binHeight));
            }
//            Set<int[]> packets = new TreeSet<>(new Comparator<int[]>() {
//                @Override
//                public int compare(int[] o1, int[] o2) {
//                    if (o1[0] == o2[0]) {
//                        return Integer.compare(o1[1], o2[1]);
//                    }
//                    return Integer.compare(o1[0], o2[0]);
//                }
//            });
            ArrayList<int[]> packets = new ArrayList<>();
            for (int i = 0; i < numPackets; ++i) {
                int w = rd.nextInt(minBinWidth) + 1;
                int h = rd.nextInt(minBinHeight) + 1;
                int[] item = {w, h};
                if (w * h < 3 || w * h > minBinWidth * minBinHeight / 4) {
//                        || packets.contains(item)) {
                    --i;
                    continue;
                }
                packets.add(item);
            }
            for (int[] packet : packets) {
                if (rd.nextBoolean()) {
                    bw.write(String.format("%d %d\n", packet[0], packet[1]));
                } else {
                    bw.write(String.format("%d %d\n", packet[1], packet[0]));
                }
            }
        }
    }

    public int getMinBins() {
        int sPackets = 0;
        for (Packet packet : packets) {
            sPackets += packet.h * packet.w;
        }
        int sBins = 0;
        for (int i = 0; i < bins.size(); ++i) {
            Bin b = bins.get(i);
            sBins += b.height * b.width;
            if (sBins >= sPackets) {
                return (i + 1);
            }
        }
        return bins.size();
    }

    public void seqLnsSolver() {
        System.out.println("LNS Solver");
        List<Packet> oldPackets = null;
        int minBins = getMinBins();
        System.out.println("Min containers = " + minBins);
        numBinsUsed = bins.size() + 1;

        while (true) {
            --numBinsUsed;
            stateModel(numBinsUsed, oldPackets);
            System.out.format("Search with %d containers:\n ", numBinsUsed);
            LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, 2, 20000, 8000, 20000, rd, new ConstraintObjective(cs), "lnsSolver(2)");
            solver.solve();
            if (NumberUtils.compare(cs.getViolation(), 0) != 0) {
                packets = oldPackets;
                break;
            }
            if (numBinsUsed == minBins) {
                break;
            }
            oldPackets = new ArrayList<>();
            for (Packet p : packets) {
                oldPackets.add(new Packet(p.w, p.h, p.x, p.y, p.r, p.b));
            }
        }
    }

    public void seqTabuSearchSolver() {
        System.out.println("Tabu search:");
        List<Packet> oldPackets = null;
        int minBins = getMinBins();
        System.out.println("Min containers = " + minBins);
        numBinsUsed = bins.size() + 1;

        while (true) {
            --numBinsUsed;
            stateModel(numBinsUsed, oldPackets);
            System.out.format("Search with %d containers:\n ", numBinsUsed);
            TabuSearch.getInstance().search(localSearchManager.getVariables(),
                    new ConstraintObjective(cs), 20, 1000, 5000, rd);
            if (NumberUtils.compare(cs.getViolation(), 0) != 0) {
                packets = oldPackets;
                break;
            }
            if (numBinsUsed == minBins) {
                break;
            }
            oldPackets = new ArrayList<>();
            for (Packet p : packets) {
                oldPackets.add(new Packet(p.w, p.h, p.x, p.y, p.r, p.b));
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Random rd = new Random();
        int numBins = 10;

        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        boolean dataGen = true;

        String filePath;
        if (dataGen) {
            int numPackets = 50;
            filePath = String.format("data/test_multi_binpacking2d_%d_%d_%d.txt", numBins, numPackets, System.nanoTime());
            genInput(filePath, numBins, numPackets, rd);
        } else {
            filePath = "data/test_multi_binpacking2d_10_50_40742123473386.txt";
        }

        MultiBinpacking2D binpacking2D = new MultiBinpacking2D(filePath);
        binpacking2D.seqTabuSearchSolver();
        binpacking2D.saveHtml(filePath + String.format(" Tabusearch_%d.html",
                NumberUtils.compare(binpacking2D.cs.getViolation(), 0.0) == 0 ?
                        binpacking2D.numBinsUsed : binpacking2D.numBinsUsed + 1));

        binpacking2D.seqLnsSolver();
        binpacking2D.saveHtml(filePath + String.format(" LnsSolver_%d.html",
                NumberUtils.compare(binpacking2D.cs.getViolation(), 0.0) == 0 ?
                        binpacking2D.numBinsUsed : binpacking2D.numBinsUsed + 1));
    }
}


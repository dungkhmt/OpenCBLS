package localsearch.applications.binpacking2D;

import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class BinPacking2D {
	public int W, H;
	public int n;
	public int[] w;
	public int[] h;

	
	LocalSearchManager ls;
	ConstraintSystem S;
	VarIntLS[] x;
	VarIntLS[] y;
	VarIntLS[] o;

	public BinPacking2D() {
	}

	public void readData(String fn) {
		try {
			Scanner in = new Scanner(new File(fn));
			W = in.nextInt();
			H = in.nextInt();
			ArrayList<Item> I = new ArrayList<Item>();
			while (true) {
				int wi = in.nextInt();
				if (wi == -1)
					break;
				int hi = in.nextInt();
				I.add(new Item(wi, hi));
			}
			in.close();

			n = I.size();
			w = new int[n];
			h = new int[n];
			for (int i = 0; i < I.size(); i++) {
				w[i] = I.get(i).w;
				h[i] = I.get(i).h;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void stateModel() {
		ls = new LocalSearchManager();
		S = new ConstraintSystem(ls);
		x = new VarIntLS[n];
		y = new VarIntLS[n];
		o = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(ls, 0, W);
			y[i] = new VarIntLS(ls, 0, H);
			o[i] = new VarIntLS(ls, 0, 1);
		}

		for (int i = 0; i < n; i++) {
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(
					new FuncPlus(x[i], w[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(
					new FuncPlus(y[i], h[i]), H)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(x[i], h[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(y[i], w[i]), H)));
		}

		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				// o[i] = 0, o[j] = 0 (no orientation)
				IConstraint[] c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(
						o[j], 0)), new OR(c)));

				// o[i] = o, o[j] = 1
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(
						o[j], 1)), new OR(c)));

				// o[i] = 1, o[j] = 0
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(
						o[j], 0)), new OR(c)));

				// o[i] = 1, o[j] = 1
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(
						o[j], 1)), new OR(c)));
			}
		}
		ls.close();
	}

	public void search(int timeLimit) {
		TabuSearch ts = new TabuSearch();
		ts.search(S, 80, timeLimit, 50000, 70);
	}

	public void print() {
		for (int i = 0; i < n; i++) {
			System.out.println("item " + (i + 1) + " :  " + x[i].getValue()
					+ " " + y[i].getValue() + " ->  " + (w[i]) + " " + (h[i])
					+ " " + o[i].getValue());
		}
	}

	public void outCanvas() {
		final String[] Color = new String[] { "#000000", "#FFFF00", "#1CE6FF",
				"#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
				"#FF0000", "#7A4900", "#0000A6", "#63FFAC", "#B79762",
				"#004D43", "#8FB0FF", "#997D87", "#5A0007", "#809693",
				"#1B4400", "#FEFFE6", "#4FC601", "#3B5DFF", "#4A3B53",
				"#FF2F80", "#61615A", "#BA0900", "#6B7900", "#00C2A0",
				"#FFAA92", "#FF90C9", "#B903AA", "#D16100", "#FFDBE5",
				"#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8",
				"#013349", "#00846F", "#372101", "#FFB500", "#C2FFED",
				"#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09",
				"#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75",
				"#B77B68", "#7A87A1", "#788D66", "#885578", "#FAD09F",
				"#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED",
				"#886F4C", "#34362D", "#B4A8BD", "#00A6AA", "#452C2C",
				"#636375", "#A3C8C9", "#FF913F", "#938A81", "#575329",
				"#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757",
				"#C8A1A1", "#1E6E00", "#7900D7", "#A77500", "#6367A9",
				"#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700",
				"#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF",
				"#99ADC0", "#3A2465", "#922329", "#5B4534", "#FDE8DC",
				"#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72",
				"#6A3A4C", "#83AB58", "#001C1E", "#D1F7CE", "#004B28",
				"#C8D0F6", "#A3A489", "#806C66", "#222800", "#BF5650",
				"#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4",
				"#1E0200", "#5B4E51", "#C895C5", "#320033", "#FF6832",
				"#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58" };
		int zoom = 35;
		try {
			File outFile = new File(
					"src/localsearch/applications/binpacking/binpacking2dCanvas.html");
			PrintWriter out;
			out = new PrintWriter(outFile);

			out.println("<!doctype html>\n<html>\n<head>\n<title>Binpacking2D</title>\n</head>\n<body>\n");
			out.println("<canvas id=\"myCanvas\" width=\"1410\" height=\"750\"> Canvas not supported </canvas>\n");
			out.println("<script type=\"text/javascript\">\n");
			out.println("var canvas=document.getElementById('myCanvas');\n");
			out.println("var ctx=canvas.getContext('2d');\n");
			// out.println("ctx.strokeRect(0,0,1400,740);\n");
			out.println("ctx.strokeRect(0,0," + W * zoom + "," + H * zoom
					+ ");");
			out.println("ctx.lineWidth=3;");
			out.println("ctx.setLineDash([2, 5]);");
			out.println("ctx.font = \"15px Arial\";");
			out.println("ctx.textAlign = \"center\";\n\n");
			for (int i = 0; i < n; i++) {
				if (o[i].getValue() == 0) {
					out.println("ctx.fillStyle='" + Color[i] + "';\n");
					out.println("ctx.fillRect(" + x[i].getValue() * zoom + ","
							+ y[i].getValue() * zoom + "," + (w[i]) * zoom
							+ "," + (h[i]) * zoom + ");\n");
					int xx = x[i].getValue() * zoom + w[i] * zoom / 2;
					int yy = y[i].getValue() * zoom + h[i] * zoom / 2;
					out.println("ctx.fillText('" + (i + 1) + "'," + xx + ","
							+ yy + ");\n\n");
				} else {
					out.println("ctx.fillStyle='" + Color[i] + "';\n");
					out.println("ctx.fillRect(" + x[i].getValue() * zoom + ","
							+ y[i].getValue() * zoom + "," + (h[i]) * zoom
							+ "," + (w[i]) * zoom + ");\n");
					int xx = x[i].getValue() * zoom + h[i] * zoom / 2;
					int yy = y[i].getValue() * zoom + w[i] * zoom / 2;
					out.println("ctx.fillText('" + (i + 1) + "'," + xx + ","
							+ yy + ");\n\n");
				}
			}
			out.println("</script>\n");
			out.println("</body></html>");
			out.close();
		} catch (IOException exx) {
			exx.printStackTrace();
		}
	}
	
	public void printResultHTML(String fn){
		int[] rx = new int[x.length];
		int[] ry = new int[y.length];
		int[] ro = new int[o.length];
		for(int i = 0; i < x.length; i++){
			rx[i] = x[i].getValue();
			ry[i] = y[i].getValue();
			ro[i] = o[i].getValue();
		}
		for(int i = 0; i < rx.length; i++){
			System.out.print(rx[i] + ",");
		}
		System.out.println();
		for(int i = 0; i < ry.length; i++){
			System.out.print(ry[i] + ",");
		}
		System.out.println();
		for(int i = 0; i < ro.length; i++){
			System.out.print(ro[i] + ",");
		}
		System.out.println();
		
		outTableNew(fn,n,w,h,rx,ry,ro);
	}
	   public void outTableNew(String fn, int n, int[] w, int[] h, int[] x, int[] y, int[] o) {
	        final String[] Color = new String[]{
	                "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
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
	        try {
	            File outFile = new File(fn);
	            PrintWriter out;
	            out = new PrintWriter(outFile);
	            out.println("<!doctype html>\n<html>\n<head>\n<title>Binpacking2D</title>\n</head>\n<body>\n");

	            boolean[] isIndex  = new boolean[n+2];

	            int size = 650 / (Math.max(W, H) + 1);
	            out.println("<style type=\"text/css\">\n" + "table, td {\n" +
	                            "\t\tborder : 1px solid black;\n" +
	                            "\t\tborder-collapse: collapse;text-align : center;\n" +
	                            "\t}\n" +
	                            "\ttd {\n" +
	                            "\t\twidth : +" + size + "px;\n" +
	                            "\t\theight: +" + size + "px;\n" +
	                            "\t}"
	            );
	            for (int i = 0; i < n; i++) {
	                out.println("td.class" + (i) + " { \n background-color:" + Color[i] + ";  \n}");
	            }
	            out.println("</style>");

	            out.println("<table>");
	            for (int i = 0; i <= H; i++) {
	                out.println("<tr>");
	                for (int j = 0; j <= W; j++) {
	                    if (i == 0) {
	                        if (j == 0) {
	                            out.print("<td>");
	                            out.println("</td>");
	                        } else {
	                            out.print("<td>");
	                            out.print(j);
	                            out.println("</td>");
	                        }
	                    } else {
	                        if (j == 0) {
	                            out.print("<td>");
	                            out.print(i);
	                            out.println("</td>");
	                        } else {
	                            boolean flag = false;
	                            for (int k = 0; k < n; k++) {
	                                int xk = x[k];//x[k].getValue();
	                                int yk = y[k];//y[k].getValue();
	                                int wk = w[k];
	                                int hk = h[k];
	                                //if (o[k].getValue() == 0) {
	                                if (o[k] == 0) {
	                                    if (j - 1 >= xk && j - 1 <= xk + wk - 1 && i - 1 >= yk && i - 1 <= yk + hk - 1) {
	                                        out.print("<td class='class" + k + "'>");

	                                        if(!isIndex[k] && (j-1)==(xk+xk+wk-1)/2 && (i-1)==(yk+yk+hk-1)/2){
	                                            out.print(k);
	                                            isIndex[k]=true;
	                                        }
	                                        flag = true;
	                                        break;
	                                    }
	                                } else {
	                                    if (j - 1 >= xk && j - 1 <= xk + hk - 1 && i - 1 >= yk && i - 1 <= yk + wk - 1) {
	                                        out.print("<td class='class" + k + "'>");
	                                        if(!isIndex[k] && (j-1)==(xk+xk+hk-1)/2 && (i-1)==(yk+yk+wk-1)/2){
	                                            out.print(k);
	                                            isIndex[k]=true;
	                                        }
	                                        flag = true;
	                                        break;
	                                    }
	                                }
	                            }
	                            if (flag) out.println("</td>");
	                            else {
	                                out.print("<td>");
	                                out.println("</td>");
	                            }
	                        }
	                    }

	                }
	                out.println("</tr>");
	            }
	            out.println("</table>");

	            out.println("</body></html>");
	            out.close();
	        } catch (IOException exx) {
	            exx.printStackTrace();
	        }
	    }

	public void outTable(String filename) {
		final String[] Color = new String[] { "#000000", "#FFFF00", "#1CE6FF",
				"#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
				"#FF0000", "#7A4900", "#0000A6", "#63FFAC", "#B79762",
				"#004D43", "#8FB0FF", "#997D87", "#5A0007", "#809693",
				"#1B4400", "#FEFFE6", "#4FC601", "#3B5DFF", "#4A3B53",
				"#FF2F80", "#61615A", "#BA0900", "#6B7900", "#00C2A0",
				"#FFAA92", "#FF90C9", "#B903AA", "#D16100", "#FFDBE5",
				"#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8",
				"#013349", "#00846F", "#372101", "#FFB500", "#C2FFED",
				"#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09",
				"#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75",
				"#B77B68", "#7A87A1", "#788D66", "#885578", "#FAD09F",
				"#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED",
				"#886F4C", "#34362D", "#B4A8BD", "#00A6AA", "#452C2C",
				"#636375", "#A3C8C9", "#FF913F", "#938A81", "#575329",
				"#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757",
				"#C8A1A1", "#1E6E00", "#7900D7", "#A77500", "#6367A9",
				"#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700",
				"#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF",
				"#99ADC0", "#3A2465", "#922329", "#5B4534", "#FDE8DC",
				"#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72",
				"#6A3A4C", "#83AB58", "#001C1E", "#D1F7CE", "#004B28",
				"#C8D0F6", "#A3A489", "#806C66", "#222800", "#BF5650",
				"#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4",
				"#1E0200", "#5B4E51", "#C895C5", "#320033", "#FF6832",
				"#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58" };
		try {
			File outFile = new File(filename);
			PrintWriter out;
			out = new PrintWriter(outFile);
			out.println("<!doctype html>\n<html>\n<head>\n<title>Binpacking2D</title>\n</head>\n<body>\n");

			int size = 650 / (Math.max(W, H) + 1);
			out.println("<style type=\"text/css\">\n" + "table, td {\n"
					+ "\t\tborder : 1px solid black;\n"
					+ "\t\tborder-collapse: collapse;text-align : center;\n"
					+ "\t}\n" + "\ttd {\n" + "\t\twidth : +" + size + "px;\n"
					+ "\t\theight: +" + size + "px;\n" + "\t}");
			for (int i = 0; i < n; i++) {
				out.println("td.class" + (i) + " { \n background-color:"
						+ Color[i] + "; \n}");
			}
			out.println("</style>");

			out.println("<table>");
			for (int i = 0; i <= H; i++) {
				out.println("<tr>");
				for (int j = 0; j <= W; j++) {
					if (i == 0) {
						if (j == 0) {
							out.print("<td>");
							out.println("</td>");
						} else {
							out.print("<td>");
							out.print(j-1);
							out.println("</td>");
						}
					} else {
						if (j == 0) {
							out.print("<td>");
							out.print(i-1);
							out.println("</td>");
						} else {
							boolean flag = false;
							for (int k = 0; k < n; k++) {
								int xk = x[k].getValue();
								int yk = y[k].getValue();
								int wk = w[k];
								int hk = h[k];
								if (o[k].getValue() == 0) {
									if (j - 1 >= xk && j - 1 <= xk + wk - 1
											&& i - 1 >= yk
											&& i - 1 <= yk + hk - 1) {
										out.print("<td class='class" + k + "'>");
										//out.print(k);
										flag = true;
										break;
									}
								} else {
									if (j - 1 >= xk && j - 1 <= xk + hk - 1
											&& i - 1 >= yk
											&& i - 1 <= yk + wk - 1) {
										out.print("<td class='class" + k + "'>");
										//out.print(k);
										flag = true;
										break;
									}
								}
							}
							if (flag)
								out.println("</td>");
							else {
								out.print("<td>");
								out.println("</td>");
							}
						}
					}

				}
				out.println("</tr>");
			}
			out.println("</table>");

			out.println("</body></html>");
			out.close();
		} catch (IOException exx) {
			exx.printStackTrace();
		}
	}

	public boolean solve(int timeLimit) {
		stateModel();
		search(timeLimit);
		print();
		// outCanvas();
		//outTable("binpacking2D.html");
		return S.violations() == 0;
	}

	public void testBatch(String filename, int nbTrials, int timeLimit) {
		BinPacking2D bp = new BinPacking2D();
		bp.readData(filename);
		double[] t = new double[nbTrials];
		double avg_t = 0;
		int nbSolved = 0;
		for (int k = 0; k < nbTrials; k++) {
			double t0 = System.currentTimeMillis();
			boolean ok = bp.solve(timeLimit);
			
			t[k] = (System.currentTimeMillis() - t0) * 0.001;
			if (ok) {
				nbSolved++;
				avg_t += t[k];
			}
		}
		avg_t = avg_t * 1.0 / nbSolved;
		System.out.println("Time = " + avg_t + ", nbSolved = " + nbSolved);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 BinPacking2D bp = new BinPacking2D();
		 //bp.readData("data\\BinPacking2D\\bin-packing-2D-W19-H20-I21.txt");
		 bp.readData("data\\BinPacking2D\\bin-packing-2D-W19-H19-I21.txt");
		 bp.solve(1000);
		 //bp.outTableNew("OpenCBLS-binpacking2D.html");
		 bp.printResultHTML("Binpacking2D.html");
		/*
		BinPacking2D bp = new BinPacking2D();
		bp.testBatch("data\\BinPacking2D\\bin-packing-2D-W19-H19-I21.txt", 10,300);
		 */
	}

}
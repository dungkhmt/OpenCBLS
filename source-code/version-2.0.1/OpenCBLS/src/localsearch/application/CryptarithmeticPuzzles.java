package localsearch.application;

import localsearch.constraint.AllDifferent;
import localsearch.constraint.basic.operator.Equal;
import localsearch.function.math.FuncMult;
import localsearch.function.math.FuncPlus;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.LnsSolver;

import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class CryptarithmeticPuzzles {

    public static void main(String[] args) {
        LocalSearchManager localSearchManager = new LocalSearchManager();
        ConstraintSystem cs = new ConstraintSystem();
        Random rd = new Random();

        VarIntLS t = new VarIntLS(1, 9, localSearchManager, rd);
        VarIntLS w = new VarIntLS(0, 9, localSearchManager, rd);
        VarIntLS o = new VarIntLS(0, 9, localSearchManager, rd);
        VarIntLS f = new VarIntLS(1, 9, localSearchManager, rd);
        VarIntLS u = new VarIntLS(0, 9, localSearchManager, rd);
        VarIntLS r = new VarIntLS(0, 9, localSearchManager, rd);

        VarIntLS c1 = new VarIntLS(0, 1, localSearchManager, rd);
        VarIntLS c2 = new VarIntLS(0, 1, localSearchManager, rd);
        VarIntLS c3 = new VarIntLS(0, 1, localSearchManager, rd);

        // O + O = R + 10xc1
        cs.post(new Equal(
                new FuncPlus(o, o),
                new FuncPlus(new FuncMult(c1, 10), r)
        ));

        // W + W + c1 = U + 10xc2
        cs.post(new Equal(
                new FuncPlus(new FuncPlus(w, w), c1),
                new FuncPlus(
                        new FuncVar(u),
                        new FuncMult(c2, 10)
                )
        ));

        // T + T + c2 = O + 10xc3
        cs.post(new Equal(
                new FuncPlus(new FuncPlus(t, t), c2),
                new FuncPlus(
                        new FuncVar(o),
                        new FuncMult(c3, 10)
                )
        ));

        // c3 = f
        cs.post(new Equal(c3, f));

        cs.post(new AllDifferent(t, w, o, f, u, r));

        cs.close();
        localSearchManager.close();

        LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, cs);
        solver.solve();

        System.out.format("\t\t%d\t%d\t%d\n", t.getValue(), w.getValue(), o.getValue());
        System.out.format("+\t\t%d\t%d\t%d\n", t.getValue(), w.getValue(), o.getValue());
        System.out.format("=\t%d\t%d\t%d\t%d\n", f.getValue(), o.getValue(), u.getValue(), r.getValue());

    }
}

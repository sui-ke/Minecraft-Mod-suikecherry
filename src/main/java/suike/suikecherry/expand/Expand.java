package suike.suikecherry.expand;

import suike.suikecherry.SuiKe;

import suike.suikecherry.expand.futuremc.FuturemcExpand;

public class Expand {
    //执行联动
    public static void expand() {
        /*未来的MC*/if (Examine.FuturemcID) {FuturemcExpand.expand();}
    }
}
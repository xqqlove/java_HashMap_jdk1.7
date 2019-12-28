package hashmap_yiwa;

import java.util.*;

public class TestHashMap {
    public static void main(String[] args) {
        My_Map m=new My_HashMap();
        Map m1=new TreeMap();
        m.put("1","tre");
        m.put("2","dsf");
        m.put("3","trvxcve");
        m.put("4","trejgh");
        System.out.println(m);
        Set<My_Map.Entry> s=m.entrySet();
        for (My_Map.Entry e:s){
            System.out.println(e);
        }

        List l=new ArrayList();
        Set s1=new HashSet();
        s1.add(1);
        s1.add(2);
        s1.add(3);
        s1.add(4);
        for (Object e:s1){
            System.out.println(e);
        }
    }
}

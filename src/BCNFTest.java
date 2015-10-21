import org.junit.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

public class BCNFTest {
    /**
     * Performs a basic test on a simple table.
     * gives input attributes (a,b,c) and functional dependency a->c
     * and expects output (a,c),(b,c) or any reordering
     **/
    @Test
    public void testSimpleBCNF() {
        //construct table
        AttributeSet attrs = new AttributeSet();
        attrs.addAttribute(new Attribute("a"));
        attrs.addAttribute(new Attribute("b"));
        attrs.addAttribute(new Attribute("c"));

        //create functional dependencies
        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
        AttributeSet ind = new AttributeSet();
        AttributeSet dep = new AttributeSet();
        ind.addAttribute(new Attribute("a"));
        dep.addAttribute(new Attribute("c"));
        FunctionalDependency fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        //run client code
        Set<AttributeSet> bcnf = BCNF.decompose(attrs, fds);

        //verify output
        assertEquals("Incorrect number of tables", 2, bcnf.size());

        for (AttributeSet as : bcnf) {
            assertEquals("Incorrect number of attributes", 2, as.size());
            assertTrue("Incorrect table", as.contains(new Attribute("a")));
        }

    }

    @Test
    public void testAttributeSetEquals() {
        Attribute a = new Attribute("a");
        Attribute b = new Attribute("b");
        Attribute a2 = new Attribute("a");

        assertTrue(a.equals(a2));
        assertTrue(a2.equals(a));
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

    @Test
    public void testPowerSet() {
        AttributeSet s = new AttributeSet();
        Attribute a = new Attribute("a");
        Attribute b = new Attribute("b");
        Attribute c = new Attribute("c");
        s.addAttribute(a);
        s.addAttribute(b);
        s.addAttribute(c);

        Iterator<AttributeSet> iter = s.powerSet();
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        }
        assertEquals(8, count);
    }

    public void testCorrectAssumptions() {
        //test attribute create and equals
        Attribute a = new Attribute("a");
        Attribute b = new Attribute("b");
        Attribute aa = new Attribute("a");

        assertEquals(a,aa);
        assertEquals(aa,a);
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));

        //test attribute sets equals and addAttribute
        AttributeSet as = new AttributeSet();
        AttributeSet bs = new AttributeSet();

        as.addAttribute(a);
        as.addAttribute(b);

        assertFalse(bs.iterator().hasNext());

        //test basics of fds
        FunctionalDependency fd = new FunctionalDependency(as,bs);
        assertEquals(as,fd.independent());
        assertEquals(bs,fd.dependent());

        //test attribute set iterator
        Iterator<Attribute> iter = as.iterator();
        boolean seenA = false;
        boolean seenB = false;

        for(int i = 0; i < 2; i++) {
            assertTrue(iter.hasNext());
            Attribute r = iter.next();
            if(r.equals(a))
                seenA = true;
            if(r.equals(b))
                seenB = true;
        }
        assertFalse(iter.hasNext());
        assertTrue(seenA);
        assertTrue(seenB);
    }

    public void testNoDecompositionBCNF() {
        //a,b,c,d
        // no FD's
        AttributeSet attrs = new AttributeSet();
        attrs.addAttribute(new Attribute("a"));
        attrs.addAttribute(new Attribute("b"));
        attrs.addAttribute(new Attribute("c"));
        attrs.addAttribute(new Attribute("d"));

        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();

        Set<AttributeSet> res = BCNF.decompose(attrs, fds);

        assertEquals(1, res.size());

        boolean[] seen = new boolean[4];
        for(AttributeSet as : res) {
            Iterator<Attribute> iter = as.iterator();

            while(iter.hasNext()) {
                Attribute a = iter.next();

                if(a.equals(new Attribute("a")))
                    seen[0] = true;
                else if(a.equals(new Attribute("b")))
                    seen[1] = true;
                else if(a.equals(new Attribute("c")))
                    seen[2] = true;
                else if(a.equals(new Attribute("d")))
                    seen[3] = true;
                else
                    fail();
            }
        }

        for(int i = 0; i < 4; i++)
            assertTrue(seen[i]);
    }

    public void testUniqueDecompositionBCNF() {
        //a,b,c,d
        //a->b
        AttributeSet attrs = new AttributeSet();
        attrs.addAttribute(new Attribute("a"));
        attrs.addAttribute(new Attribute("b"));
        attrs.addAttribute(new Attribute("c"));
        attrs.addAttribute(new Attribute("d"));

        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
        AttributeSet ind = new AttributeSet();
        AttributeSet dep = new AttributeSet();
        ind.addAttribute(new Attribute("a"));
        dep.addAttribute(new Attribute("b"));
        FunctionalDependency fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        Set<AttributeSet> res = BCNF.decompose(attrs, fds);

        assertEquals(2, res.size());

        for(AttributeSet as : res) {
            Iterator<Attribute> iter = as.iterator();

            boolean[] seen = new boolean[4];
            while(iter.hasNext()) {
                Attribute a = iter.next();

                if(a.equals(new Attribute("a")))
                    seen[0] = true;
                else if(a.equals(new Attribute("b")))
                    seen[1] = true;
                else if(a.equals(new Attribute("c")))
                    seen[2] = true;
                else if(a.equals(new Attribute("d")))
                    seen[3] = true;
                else
                    fail();
            }

            if(seen[1]) {
                assertTrue(seen[0]);
                assertFalse(seen[2]);
                assertFalse(seen[3]);
            } else {
                assertTrue(seen[0]);
                assertTrue(seen[2]);
                assertTrue(seen[3]);
            }
        }
    }

    public void testRecursionBCNF() {
        //a,b,c,d
        //a -> b, c -> d
        AttributeSet attrs = new AttributeSet();
        attrs.addAttribute(new Attribute("a"));
        attrs.addAttribute(new Attribute("b"));
        attrs.addAttribute(new Attribute("c"));
        attrs.addAttribute(new Attribute("d"));

        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
        AttributeSet ind = new AttributeSet();
        AttributeSet dep = new AttributeSet();
        ind.addAttribute(new Attribute("a"));
        dep.addAttribute(new Attribute("b"));
        FunctionalDependency fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        ind = new AttributeSet();
        dep = new AttributeSet();
        ind.addAttribute(new Attribute("c"));
        dep.addAttribute(new Attribute("d"));
        fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        assertTrue(BCNF.isCorrect(attrs, fds));

    }

    public void testTransitivityBCNF() {
        //0,1,2,3,4,5,6,7,8,9
        //i->(i+1) all i < 7

        //this tests they take the closure before decomposing
        //all correct responses contain the table 0,8,9
        //as 0 the only key, but naive algorithms won't detect this
        //or will infinitely loop
        AttributeSet attrs = new AttributeSet();
        for(int i = 0; i < 10; i++)
            attrs.addAttribute(new Attribute("" + i));

        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();

        for(int i = 0; i < 7; i++) {
            AttributeSet ind = new AttributeSet();
            AttributeSet dep = new AttributeSet();
            ind.addAttribute(new Attribute("" + i));
            dep.addAttribute(new Attribute("" + (i+1)));
            FunctionalDependency fd = new FunctionalDependency(ind, dep);
            fds.add(fd);
        }

        assertTrue(BCNF.isCorrect(attrs, fds));
    }

    public void testComplexClosureBCNF() {
        //a,b,c,d
        //a -> x, x -> b
        AttributeSet attrs = new AttributeSet();
        attrs.addAttribute(new Attribute("a"));
        attrs.addAttribute(new Attribute("b"));
        attrs.addAttribute(new Attribute("c"));
        attrs.addAttribute(new Attribute("d"));

        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
        AttributeSet ind = new AttributeSet();
        AttributeSet dep = new AttributeSet();
        ind.addAttribute(new Attribute("a"));
        dep.addAttribute(new Attribute("x"));
        FunctionalDependency fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        ind = new AttributeSet();
        dep = new AttributeSet();
        ind.addAttribute(new Attribute("x"));
        dep.addAttribute(new Attribute("b"));
        fd = new FunctionalDependency(ind, dep);
        fds.add(fd);


        assertTrue(BCNF.isCorrect(attrs, fds));
    }

    public void testComplexBCNF() {
        //c0,c1,c2,c3,c4,c5,c6,c7,c8,c9
        //c0 -> c1,c2
        //c3 -> c4 -> c5
        //c0 -> c0
        //c6,c7 -> c8
        AttributeSet attrs = new AttributeSet();
        for(int i = 0; i < 10; i++) {
            attrs.addAttribute(new Attribute("c" + i));
        }

        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();

        AttributeSet ind = new AttributeSet();
        AttributeSet dep = new AttributeSet();

        ind.addAttribute(new Attribute("c0"));
        dep.addAttribute(new Attribute("c1"));
        dep.addAttribute(new Attribute("c2"));
        FunctionalDependency fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        ind = new AttributeSet();
        dep = new AttributeSet();
        ind.addAttribute(new Attribute("c3"));
        dep.addAttribute(new Attribute("c4"));
        fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        ind = new AttributeSet();
        dep = new AttributeSet();
        ind.addAttribute(new Attribute("c4"));
        dep.addAttribute(new Attribute("c5"));
        fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        ind = new AttributeSet();
        dep = new AttributeSet();
        ind.addAttribute(new Attribute("c0"));
        dep.addAttribute(new Attribute("c0"));
        fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        ind = new AttributeSet();
        dep = new AttributeSet();
        ind.addAttribute(new Attribute("c6"));
        ind.addAttribute(new Attribute("c7"));
        dep.addAttribute(new Attribute("c8"));
        fd = new FunctionalDependency(ind, dep);
        fds.add(fd);

        assertTrue(BCNF.isCorrect(attrs, fds));
    }
}

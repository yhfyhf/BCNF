import com.sun.tools.doclint.HtmlTag;
import javafx.collections.transformation.TransformationList;

import java.util.*;

public class BCNF {

    /**
     * Implement your algorithm here
     **/
    public static Set<AttributeSet> decompose(AttributeSet attributeSet,
                                              Set<FunctionalDependency> functionalDependencies) {
        Iterator<AttributeSet> powerset = attributeSet.powerSet();

        while (powerset.hasNext()) {
            AttributeSet attrSet = powerset.next();
            AttributeSet closure = closure3(new AttributeSet(attrSet), functionalDependencies).intersection(attributeSet);

            if (!closure.equals(attributeSet) && !closure.equals(attrSet) && closure.size() > 0) {
                Set<AttributeSet> left = decompose(closure, functionalDependencies);  // recursively
                AttributeSet temp = attributeSet.difference(closure);
                temp.addAll(attrSet);
                Set<AttributeSet> right = decompose(temp, functionalDependencies);    // recursively
                left.addAll(right);
                return left;
            }

        }

        Set<AttributeSet> ret = new HashSet<>();
        ret.add(attributeSet);
        return ret;
    }

    /**
     * Recommended helper method
     **/
    public static AttributeSet closure(AttributeSet attributeSet, Set<FunctionalDependency> functionalDependencies) {
        // TODO: implement me!
        if (functionalDependencies.isEmpty()) {
            return attributeSet;
        }

        boolean hasChanged = false;

        Iterator<FunctionalDependency> iter = functionalDependencies.iterator();
        while (true) {
            while (!iter.hasNext()) {
                if (hasChanged) {
                    hasChanged = false;
                    iter = functionalDependencies.iterator();
                } else {
                    return attributeSet;
                }
            }

            FunctionalDependency functionalDependency = iter.next();
            if (attributeSet.containsAll(functionalDependency.independent())) {
                int size = attributeSet.size();
                attributeSet.addAll(functionalDependency.dependent());
                hasChanged = attributeSet.size() > size;
            }
        }
    }

    public static AttributeSet closure2(AttributeSet attributeSet, Set<FunctionalDependency> functionalDependencies) {
        AttributeSet oldDep = new AttributeSet();
        AttributeSet newDep = attributeSet;

        while (newDep.size() > oldDep.size()) {
            oldDep = newDep;
            for (FunctionalDependency fd : functionalDependencies) {
                if (newDep.containsAll(fd.independent())) {
                    newDep.addAll(fd.dependent());
                }
            }
        }

        return newDep;
    }

    public static AttributeSet closure3(AttributeSet attributeSet, Set<FunctionalDependency> functionalDependencies) {
        AttributeSet tmp = attributeSet;
        AttributeSet result;

        do{
            result = new AttributeSet(tmp);
            Iterator<FunctionalDependency> iterfd = functionalDependencies.iterator();
            while (iterfd.hasNext()) {
                FunctionalDependency fd = iterfd.next();
                AttributeSet left = fd.independent();

                Iterator<Attribute> iter = left.iterator();
                boolean leftInAttrs = true;
                while (iter.hasNext()) {
                    if (!tmp.contains(iter.next())) {
                        leftInAttrs = false;
                        break;
                    }
                }
                if (leftInAttrs)
                    tmp.addAll(fd.dependent());
            }
        }while(!result.equals(tmp));

        return result;
    }

    public static AttributeSet closure4(AttributeSet attributeSet, Set<FunctionalDependency> functionalDependencies) {
        // Initialization
        Map<FunctionalDependency, Integer> count = new HashMap<>();
        Map<Attribute, List<FunctionalDependency>> list = new HashMap<>();
        Set<Attribute> tempSet = new HashSet<>();

        for (FunctionalDependency fd : functionalDependencies) {
            int size = fd.independent().size();
            count.put(fd, size);
            /*for (Attribute attr : fd.independent().getAttributes()) {
                if (!list.containsKey(attr)) {
                    list.put(attr, new ArrayList<>());
                } else {
                    if (!list.get(attr).contains(fd)) {
                        list.get(attr).add(fd);
                    }
                }
            }*/
            tempSet.addAll(fd.independent().getAttributes());
        }

        for (Attribute attr : tempSet) {
            List<FunctionalDependency> temp = new ArrayList<>();
            for (FunctionalDependency fd : functionalDependencies) {
                if (fd.independent().contains(attr)) {
                    temp.add(fd);
                }
            }
            list.put(attr, temp);
        }

        List<Attribute> newDep = new ArrayList<>(attributeSet.getAttributes());
        List<Attribute> update = new ArrayList<>(attributeSet.getAttributes());

        // Computation
        while (!update.isEmpty()) {
            int randomIndex = new Random().nextInt(update.size());
            Attribute attr = update.get(randomIndex);
            update.remove(randomIndex);
            if (list.containsKey(attr)) {
                for (FunctionalDependency fd : list.get(attr)) {
                    count.put(fd, count.get(fd) - 1);
                    if (count.get(fd) == 0) {
                        List<Attribute> add = new ArrayList(fd.dependent().getAttributes());
                        add.removeAll(newDep);
                        newDep.addAll(add);
                        update.addAll(add);
                    }
                }
            }
        }

        return new AttributeSet(newDep);
    }

    public static boolean isCorrect(AttributeSet attrs, Set<FunctionalDependency> fds) {
        Set<AttributeSet> output = BCNF.decompose(attrs, fds);

        //check that all tables are completely decomposed
        Set<AttributeSet> result = new HashSet<AttributeSet>();
        for(AttributeSet as : output) {
            //copy all output to solution types
            Iterator<Attribute> iter = as.iterator();
            AttributeSet tas = new AttributeSet();
            while(iter.hasNext()) {
                tas.addAttribute(iter.next());
            }
            result.add(tas);

            if (!decompose(tas, fds).contains(tas)) {
                System.out.println(as + " Further decomposable");
                return false;
            }
        }

        //copy original table to keep all types solution types
        AttributeSet table = new AttributeSet();
        {
            Iterator<Attribute> iter = attrs.iterator();
            while(iter.hasNext())
                table.addAttribute(iter.next());
        }

        //only check their tables are covering
        boolean[] seen = new boolean[table.size()];
        for(AttributeSet as : result) {
            Iterator<Attribute> iter = as.iterator();
            while(iter.hasNext()) {
                Attribute cur = iter.next();
                boolean goodattr = false;

                Iterator<Attribute> tableiter = table.iterator();
                int index = 0;
                while(tableiter.hasNext()) {
                    if(cur.equals(tableiter.next())) {
                        seen[index] = true;
                        goodattr = true;
                    }
                    index++;
                }

                if(!goodattr)
                    return false;
            }
        }

        for(int i = 0; i < seen.length; i++) {
            if(!seen[i])
                return false;
        }

        return true;
    }
}

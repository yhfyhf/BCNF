import java.util.*;
import java.util.stream.Collectors;

/**
 * An unordered set of Attributes. This could very easily be a Java collection,
 * but an important operation (namely examining the powerset) is not easily done
 * with the Java collection.
 **/
public class AttributeSet {

	//a list of the backing attributes
	private final List<Attribute> _attributes;

	//construct an empty AttributeSet
	public AttributeSet() {
		_attributes = new ArrayList<>();
	}

	//copy constructor
	public AttributeSet(AttributeSet other) {
		_attributes = new ArrayList<>(other._attributes);
	}

    // coustructor from List
    public AttributeSet(List<Attribute> other) {
        _attributes = new ArrayList<>(other);
    }

    public List<Attribute> getAttributes() {
        return _attributes;
    }

	public void addAttribute(Attribute a) {
		if(!_attributes.contains(a))
			_attributes.add(a);
	}

    public void addAll(AttributeSet other) {
        for (Attribute attr : other._attributes) {
            addAttribute(attr);
        }
    }

	public boolean contains(Attribute a) {
		return _attributes.contains(a);
	}

    public boolean containsAll(AttributeSet other) {
        return _attributes.containsAll(other._attributes);
    }

	public int size() {
		return _attributes.size();
	}

	public boolean equals(Object other) {
		if(other == null || !(other instanceof AttributeSet)){
			return false;
		}
		//TODO: you should probably implement this
        List<Attribute> otherAttrs = ((AttributeSet) other)._attributes;
        return _attributes.containsAll(otherAttrs) && otherAttrs.containsAll(_attributes);
//        return this == other;
	}

	public Iterator<Attribute> iterator() {
		return _attributes.iterator();
	}

	public String toString() {
		String out = "";
		Iterator<Attribute> iter = iterator();
		while(iter.hasNext())
			out += iter.next() + "\t";

		return out;
	}

    /**
     * Returns an Iterator that iterates the power set of attributes.
     */
    public Iterator<AttributeSet> powerSet() {
        /**
         * Using DFS does not work because it is not ordered.
         */
        /*List<List<Attribute>> res = new ArrayList<>();
        powerSet(_attributes, 0, new ArrayList<>(), res);
        return new Iterator<AttributeSet>() {
            private int cur = 0;

            @Override
            public boolean hasNext() {
                return cur < res.size();
            }

            @Override
            public AttributeSet next() {
                AttributeSet ret = new AttributeSet();
                ret._attributes.addAll(res.get(cur++).stream().collect(Collectors.toList()));
                return ret;
            }
        };*/
        return new Iterator<AttributeSet>() {
            private long current = 0, size = (1L << (_attributes.size()));

            public boolean hasNext() {
                return current < size;
            }

            public AttributeSet next() {
                // Uses bit manipulation.
                // {}, {0}, {1}, {0, 1}......
                AttributeSet ret = new AttributeSet();
                for (int i = 0; i < _attributes.size(); i++) {
                    if (((1L << i) & current) != 0) {
                        ret._attributes.add(_attributes.get(i));
                    }
                }
                current++;
                return ret;
            }
        };
    }

    /**
     * Helper function for powerSet that recursively add elements to power set.
     */
    @Deprecated
    private void powerSet(List<Attribute> attributes, int index, List<Attribute> cur, List<List<Attribute>> res) {
        if (index == attributes.size()) {
            res.add(new ArrayList<>(cur));
            return;
        }
        cur.add(attributes.get(index));
        powerSet(attributes, index + 1, cur, res);
        cur.remove(cur.size() - 1);
        powerSet(attributes, index + 1, cur, res);
    }

    /**
     * Returns the intersection of AttributeSets this and other.
     */
    public AttributeSet intersection(AttributeSet other) {
        AttributeSet ret = new AttributeSet();
        other._attributes.stream().filter(attr -> contains(attr)).forEach(ret::addAttribute);
        return ret;
    }

    /**
     * Returns the difference of AttributeSet this and other.
     */
    public AttributeSet difference(AttributeSet other) {
        AttributeSet ret = new AttributeSet(this);
        other._attributes.stream().filter(attr -> contains(attr)).forEach(ret._attributes::remove);
        return ret;
    }
}

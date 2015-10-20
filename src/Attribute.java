
/**
 * Represents a single attribute, in a real database would be more interesting, but
 * here it is just backed by a string and exposes some of the logical methods.
 *
 * An Attribute is immutable
 **/
public class Attribute {
	private final String _name;

	public Attribute(String name) {
		_name = name;
	}

	public String toString() {
		return _name;
	}

	public boolean equals(Object other) {
		if(other == null)
			return false;
		else if(!(other instanceof Attribute))
			return false;
		return _name.equals(((Attribute)other)._name);
	}

	public int hashCode() {
		return _name.hashCode();
	}
}

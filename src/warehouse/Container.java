package warehouse;

public class Container {
	private int id;
	
	public Container(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}

	// Containers is used in lists and can be used in future in other storages
	// so the entity must have the methods to differ, search and compare
	// added equals, hashcode and toString (to debug)

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Container container = (Container) o;

		return id == container.id;

	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "Container{" +
				"id=" + id +
				'}';
	}
}

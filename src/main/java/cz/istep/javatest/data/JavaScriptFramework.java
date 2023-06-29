package cz.istep.javatest.data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class JavaScriptFramework {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Size(min = 3, max = 30)
	private String name;

	@Enumerated(EnumType.STRING)
	private HypeLevel hypeLevel;

	@OneToMany(mappedBy="javaScriptFramework", fetch=FetchType.EAGER)
	private List<Version> versions = new ArrayList<>();

	public JavaScriptFramework() {
	}

	public JavaScriptFramework(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "JavaScriptFramework [id=" + id + ", name=" + name + "]";
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public void setHypeLevel(HypeLevel hypeLevel) {
		this.hypeLevel = hypeLevel;
	}

	public HypeLevel getHypeLevel() {
		return hypeLevel;
	}
}

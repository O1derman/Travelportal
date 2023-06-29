package cz.istep.javatest.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="framework_id", referencedColumnName="id", nullable=false)
    private JavaScriptFramework javaScriptFramework;

    private String versionNumber;

    private String releaseDate;

    // Constructors, Getters, and Setters
    public Version() {}

    public Version(String versionNumber, String releaseDate) {
        this.versionNumber = versionNumber;
        this.releaseDate = releaseDate;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public JavaScriptFramework getJavaScriptFramework() {
        return javaScriptFramework;
    }

    public void setJavaScriptFramework(JavaScriptFramework javaScriptFramework) {
        this.javaScriptFramework = javaScriptFramework;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

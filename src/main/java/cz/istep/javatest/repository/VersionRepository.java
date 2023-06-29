package cz.istep.javatest.repository;


import cz.istep.javatest.data.JavaScriptFramework;
import cz.istep.javatest.data.Version;
import org.springframework.data.repository.CrudRepository;

public interface VersionRepository extends CrudRepository<Version, Long> {



}

package cz.istep.javatest.controller;

import cz.istep.javatest.data.JavaScriptFramework;
import cz.istep.javatest.data.Version;
import cz.istep.javatest.repository.JavaScriptFrameworkRepository;
import cz.istep.javatest.repository.VersionRepository;
import cz.istep.javatest.rest.Errors;
import cz.istep.javatest.rest.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class JavaScriptFrameworkController {

	private final JavaScriptFrameworkRepository repository;
	private final VersionRepository versionRepository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository, VersionRepository versionRepository) {
		this.repository = repository;
		this.versionRepository = versionRepository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}

	@PostMapping("/frameworks")
	public ResponseEntity<JavaScriptFramework> createFramework(@RequestBody JavaScriptFramework framework) {
			JavaScriptFramework savedFramework = repository.save(framework);
		for (Version version : framework.getVersions()) {
			version.setJavaScriptFramework(savedFramework);
			versionRepository.save(version);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(savedFramework);
	}


	@PutMapping("/{id}")
	public ResponseEntity<JavaScriptFramework> updateFramework(@PathVariable Long id, @RequestBody JavaScriptFramework framework) {
		return repository.findById(id).map(existingFramework -> {
			existingFramework.setName(framework.getName());
			existingFramework.setHypeLevel(framework.getHypeLevel());
			repository.save(existingFramework);
			existingFramework.getVersions().forEach(existingVersion -> {
				Version version = framework.getVersions().stream()
						.filter(v -> existingVersion.getId().equals(v.getId()))
						.findAny()
						.orElse(null);
				if(version == null)
				versionRepository.deleteById(existingVersion.getId());
			});

			for (Version version : framework.getVersions()) {
				version.setJavaScriptFramework(framework);
				versionRepository.save(version);
			}
			return new ResponseEntity<>(existingFramework, HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFramework(@PathVariable Long id) {
		repository.findById(id).ifPresent(repository::delete);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


}

package cz.istep.javatest.controller;

import cz.istep.javatest.data.JavaScriptFramework;
import cz.istep.javatest.repository.JavaScriptFrameworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JavaScriptFrameworkController {

	private final JavaScriptFrameworkRepository repository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}

	@PostMapping("/frameworks")
	public ResponseEntity<JavaScriptFramework> createFramework(@RequestBody JavaScriptFramework framework) {
		JavaScriptFramework savedFramework = repository.save(framework);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedFramework);
	}


	@PutMapping("/{id}")
	public ResponseEntity<JavaScriptFramework> updateFramework(@PathVariable Long id, @RequestBody JavaScriptFramework framework) {
		return repository.findById(id).map(existingFramework -> {
			existingFramework.setName(framework.getName());
			existingFramework.setHypeLevel(framework.getHypeLevel());
			existingFramework.setVersions(framework.getVersions());
			repository.save(existingFramework);
			return new ResponseEntity<>(existingFramework, HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFramework(@PathVariable Long id) {
		repository.findById(id).ifPresent(repository::delete);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


}

package cz.istep.javatest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.istep.javatest.data.HypeLevel;
import cz.istep.javatest.data.JavaScriptFramework;
import cz.istep.javatest.data.Version;
import cz.istep.javatest.repository.JavaScriptFrameworkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JavaScriptFrameworkTests {

	@Autowired
	private MockMvc mockMvc;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private JavaScriptFrameworkRepository repository;

	@Before
	public void prepareData() throws Exception {
		repository.deleteAll();

		JavaScriptFramework react = new JavaScriptFramework("React");
		JavaScriptFramework vue = new JavaScriptFramework("Vue.js");
		
		repository.save(react);
		repository.save(vue);
	}

	@Test
	public void frameworksTest() throws Exception {
		prepareData();

		mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is("React")))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));
	}
	
	@Test
	public void addFrameworkInvalid() throws Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
//		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
//				.andExpect(status().isBadRequest())
//				.andExpect(jsonPath("$.errors", hasSize(1)))
//				.andExpect(jsonPath("$.errors[0].field", is("name")))
//				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));
		
		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].message", is("Size")));
	}

	@Test
	public void createFrameworkWithVersionsAndHypeLevel() throws Exception {
		prepareData();

		JavaScriptFramework framework = new JavaScriptFramework();
		framework.setName("Angular");

		Version v1 = new Version();
		v1.setVersionNumber("1.0.0");
		v1.setReleaseDate("2010-10-20");

		Version v2 = new Version();
		v2.setVersionNumber("2.0.0");
		v2.setReleaseDate("2016-09-14");

		List<Version> versions = new ArrayList<>();
		versions.add(v1);
		versions.add(v2);

		framework.setVersions(versions);
		framework.setHypeLevel(HypeLevel.HIGH);

		mockMvc.perform(post("/frameworks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("Angular")))
				.andExpect(jsonPath("$.hypeLevel", is("HIGH")))
				.andExpect(jsonPath("$.versions", hasSize(2)));
	}


	@Test
	public void updateFrameworkWithVersionsAndHypeLevel() throws Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		framework.setName("Angular");

		Version v1 = new Version();
		v1.setVersionNumber("16.0.0");
		v1.setReleaseDate("2017-09-26");
		v1.setJavaScriptFramework(framework);


		Version v2 = new Version();
		v2.setVersionNumber("17.0.0");
		v2.setReleaseDate("2020-10-20");
		v2.setJavaScriptFramework(framework);


		List<Version> versions = new ArrayList<>();
		versions.add(v1);
		versions.add(v2);

		framework.setVersions(versions);
		framework.setHypeLevel(HypeLevel.MEDIUM);

		// Create the framework using mockMvc
		mockMvc.perform(post("/frameworks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isCreated());

		// Retrieve all frameworks from the repository
		List<JavaScriptFramework> frameworks = (List<JavaScriptFramework>) repository.findAll();

		// Find the framework to update by name
		JavaScriptFramework savedFramework = frameworks.stream()
				.filter(f -> f.getName().equals("Angular"))
				.findFirst()
				.orElse(null);

		assertNotNull(savedFramework);

		// Update the framework
		savedFramework.setName("React Native");
		savedFramework.setHypeLevel(HypeLevel.HIGH);
		savedFramework.getVersions().remove(1);

		// Send the update request using mockMvc
		mockMvc.perform(MockMvcRequestBuilders.put("/{id}", savedFramework.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(savedFramework)))
				.andExpect(status().isOk());

		// Retrieve the updated framework from the repository
		JavaScriptFramework updatedFramework = repository.findById(savedFramework.getId()).orElse(null);

		assertNotNull(updatedFramework);
		assertEquals(savedFramework.getId(), updatedFramework.getId());
		assertEquals("React Native", updatedFramework.getName());
		assertEquals(HypeLevel.HIGH, updatedFramework.getHypeLevel());
		assertEquals(1, updatedFramework.getVersions().size());
	}




	@Test
	public void deleteFrameworkWithVersions() {
		JavaScriptFramework framework = new JavaScriptFramework();
		framework.setName("Vue.js");

		Version v1 = new Version();
		v1.setVersionNumber("2.6.0");
		v1.setReleaseDate("2019-02-14");

		Version v2 = new Version();
		v2.setVersionNumber("3.0.0");
		v2.setReleaseDate("2020-09-18");

		List<Version> versions = new ArrayList<>();
		versions.add(v1);
		versions.add(v2);

		framework.setVersions(versions);
		framework.setHypeLevel(HypeLevel.LOW);

		JavaScriptFramework savedFramework = repository.save(framework);

		Long frameworkId = savedFramework.getId();

		repository.deleteById(frameworkId);

		assertFalse(repository.existsById(frameworkId));
	}


}

package com.example.api.repository;

import com.example.student.Gender;
import com.example.student.Student;
import com.example.student.StudentRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentRepositoryTest {
    @Autowired
    private StudentRepository studentRepository;

    @BeforeAll
    void init() {
        System.out.println("Test class: " + StudentRepositoryTest.class.getSimpleName());
    }
    @AfterAll
    void done() {
        System.out.println("Test class: " + StudentRepositoryTest.class.getSimpleName() + " finished.");
    }

    @BeforeEach
    void setUp(TestReporter testReporter, TestInfo testInfo) {
        System.out.println("Starting test...");
        testReporter.publishEntry("Running " + testInfo.getDisplayName());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished.");
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should select student by email when email exists")
    void ExistsEmail() {
        Student student = new Student(
                "John",
                "John@gmail.com",
                Gender.MALE);
        studentRepository.save(student);
        Boolean result = studentRepository.selectExistsEmail(student.getEmail());
        assertThat(result).isTrue();

    }
}

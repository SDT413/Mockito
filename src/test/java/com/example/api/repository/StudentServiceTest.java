package com.example.api.repository;

import com.example.student.Gender;
import com.example.student.Student;
import com.example.student.StudentRepository;
import com.example.student.StudentService;
import com.example.student.exception.BadRequestException;
import com.example.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    private StudentService studentService;

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
        studentService = new StudentService(studentRepository);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished.");
    }


    @Test
    void getAllStudents() {
       studentService.getAllStudents();
        verify(studentRepository).findAll();
    }

    @Test
    void addStudent() {
        Student student = Student.builder().name("John").email("Email").gender(Gender.MALE).build();
        studentService.addStudent(student);
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student);
    }
    @Test
    @DisplayName("Should throw when email is taken")
    void addStudentThrow() {
        Student student = new Student(
                "John",
                "John@gmail.com",
                Gender.MALE);
        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);
        assertThatThrownBy(() -> studentService.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");
        verify(studentRepository, never()).save(any());
    }

    @Test
    void deleteStudent() {
        Student student = new Student(
                "John",
                "John@gmail.com",
                Gender.MALE);
        given(studentRepository.existsById(any()))
                .willReturn(true);
        studentService.deleteStudent(student.getId());
        verify(studentRepository).deleteById(student.getId());
    }
    @Test
    void deleteStudentThrow() {
        Student student = new Student(
                "John",
                "John@gmail.com",
                Gender.MALE);
        given(studentRepository.existsById(any()))
                .willReturn(false);
        assertThatThrownBy(() -> studentService.deleteStudent(student.getId()))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + student.getId() + " does not exists");
    }
}
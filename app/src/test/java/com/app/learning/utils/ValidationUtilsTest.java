package com.app.learning.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationUtilsTest {

    @Test
    public void testValidEmail_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertTrue(ValidationUtils.isValidEmail("student.learning@domain.org"));
        assertTrue(ValidationUtils.isValidEmail("test_123@sub.domain.vn"));
    }

    @Test
    public void testInvalidEmail_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail("invalidemail"));
        assertFalse(ValidationUtils.isValidEmail("user@.com"));
        assertFalse(ValidationUtils.isValidEmail("user@domain"));
    }

    @Test
    public void testPasswordLength_validation() {
        assertFalse(ValidationUtils.isValidPassword(null));
        assertFalse(ValidationUtils.isValidPassword("12345"));
        assertTrue(ValidationUtils.isValidPassword("123456"));
        assertTrue(ValidationUtils.isValidPassword("securepassword123"));
    }

    @Test
    public void testStrongPassword_validation() {
        assertFalse(ValidationUtils.isStrongPassword("short"));
        assertFalse(ValidationUtils.isStrongPassword("onlyletters"));
        assertFalse(ValidationUtils.isStrongPassword("12345678"));
        assertTrue(ValidationUtils.isStrongPassword("Password123"));
    }

    @Test
    public void testNameValidation() {
        assertFalse(ValidationUtils.isValidName(null));
        assertFalse(ValidationUtils.isValidName(""));
        assertFalse(ValidationUtils.isValidName(" A "));
        assertTrue(ValidationUtils.isValidName("Nguyen Van A"));
    }
}

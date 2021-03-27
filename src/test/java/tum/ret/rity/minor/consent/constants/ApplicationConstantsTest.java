package tum.ret.rity.minor.consent.constants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ApplicationConstantsTest {

    @Test
    public void isMinorAsPerIdNumber() {
        Assertions.assertTrue(ApplicationConstants.isMinorAsPerIdNumber("0512315778088"));
        Assertions.assertFalse(ApplicationConstants.isMinorAsPerIdNumber("8208056458186"));
        Assertions.assertFalse(ApplicationConstants.isMinorAsPerIdNumber("056458186"));
        Assertions.assertFalse(ApplicationConstants.isMinorAsPerIdNumber(""));
        Assertions.assertFalse(ApplicationConstants.isMinorAsPerIdNumber(null));
    }

    @Test
    void rewriteUrlSafely() {
        Assertions.assertNull(ApplicationConstants.rewriteUrlSafely(""));
        Assertions.assertNull(ApplicationConstants.rewriteUrlSafely(null));
        Assertions.assertNotNull(ApplicationConstants.rewriteUrlSafely("http://localhost"));
    }

    @Test
    public void getLocalDate() {
        Assertions.assertNull(ApplicationConstants.getLocalDate("asdasd"));
        Assertions.assertNull(ApplicationConstants.getLocalDate("00000000"));
        Assertions.assertNull(ApplicationConstants.getLocalDate("1212-1212-1212"));
        Assertions.assertNull(ApplicationConstants.getLocalDate("2020-13-01"));
        Assertions.assertNull(ApplicationConstants.getLocalDate("2020-01-32"));
        Assertions.assertNull(ApplicationConstants.getLocalDate("2021-02-29"));

        Assertions.assertNotNull(ApplicationConstants.getLocalDate("2020-02-29"));
        Assertions.assertNotNull(ApplicationConstants.getLocalDate("2020-01-31"));
        Assertions.assertNotNull(ApplicationConstants.getLocalDate("20200131"));

        Assertions.assertNull(ApplicationConstants.getLocalDate(""));
        Assertions.assertNull(ApplicationConstants.getLocalDate(null));
    }

    @Test
    public void isMinor() {
        Assertions.assertTrue(ApplicationConstants.isMinor(LocalDate.parse("2010-01-01")));
        Assertions.assertFalse(ApplicationConstants.isMinor(LocalDate.parse("1980-01-01")));
        Assertions.assertFalse(ApplicationConstants.isMinor(LocalDate.now().minusYears(18)));

        Assertions.assertFalse(ApplicationConstants.isMinor(""));
    }

    @Test
    public void getDateOfBirthFromID() {
        Assertions.assertEquals(LocalDate.parse("2005-12-31"), ApplicationConstants.getDateOfBirthFromID("0512315778088"));
        Assertions.assertEquals(LocalDate.parse("1982-08-05"), ApplicationConstants.getDateOfBirthFromID("8208056458186"));
        Assertions.assertEquals(LocalDate.parse("2019-12-31"), ApplicationConstants.getDateOfBirthFromID("1912318629081"));
        Assertions.assertNull(ApplicationConstants.getDateOfBirthFromID("8208056458187"));
    }

    @Test
    public void isValidBirthDateAsPerID() {
        Assertions.assertTrue(ApplicationConstants.isValidBirthDateAsPerID("0512315778088", "2005-12-31"));
        Assertions.assertFalse(ApplicationConstants.isValidBirthDateAsPerID("012315778088", "2005-12-31"));
        Assertions.assertFalse(ApplicationConstants.isValidBirthDateAsPerID("0512315778088", "200512-31"));
        Assertions.assertFalse(ApplicationConstants.isValidBirthDateAsPerID("1912318629081", "1919-12-31"));
    }

    @Test
    public void isValidIdNumber() {
        Assertions.assertTrue(ApplicationConstants.isValidIdNumber("0512315778088"));
        Assertions.assertTrue(ApplicationConstants.isValidIdNumber("0512315778096"));
        Assertions.assertFalse(ApplicationConstants.isValidIdNumber("051231577809"));
        Assertions.assertFalse(ApplicationConstants.isValidIdNumber("05123177809"));
        Assertions.assertFalse(ApplicationConstants.isValidIdNumber("asas"));
        Assertions.assertFalse(ApplicationConstants.isValidIdNumber(""));
        Assertions.assertFalse(ApplicationConstants.isValidIdNumber(null));
    }

}

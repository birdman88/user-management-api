package com.springboottest.user_management_api.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SsnUtilTest {

    @Test
    void padSsn_PadLeadingZeros_WhenShort() {
        String ssn = "2945";
        String result = SsnUtil.padSSN(ssn);
        assertThat(result).isEqualTo("0000000000002945");
    }

    @Test
    void padSsn_ReturnSame_whenAlready16Digits() {
        String ssn = "1234567890123456";
        String result = SsnUtil.padSSN(ssn);
        assertThat(result).isEqualTo("1234567890123456");
    }

    @Test
    void padSsn_PadWithLeadingZeros_whenSingleDigit() {
        String ssn = "5";
        String result = SsnUtil.padSSN(ssn);
        assertThat(result).isEqualTo("0000000000000005");
    }

    @Test
    void padSsn_shouldHandleNull() {
        String ssn = null;
        String result = SsnUtil.padSSN(ssn);

        assertThat(result).isNull();
    }

    @Test
    void padSsn_shouldHandleEmptyString() {
        String ssn = "";
        String result = SsnUtil.padSSN(ssn);

        assertThat(result).isEmpty();
    }

    @Test
    void padSsn_shouldRemoveNonDigitsAndPad() {
        String ssn = "123-fsff3f456";
        String result = SsnUtil.padSSN(ssn);

        assertThat(result).isEqualTo("0000000001233456");
    }


}

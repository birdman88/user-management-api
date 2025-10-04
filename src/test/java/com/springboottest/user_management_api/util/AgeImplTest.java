package com.springboottest.user_management_api.util;

import com.springboottest.user_management_api.util.validator.annotation.age.Age;
import com.springboottest.user_management_api.util.validator.annotation.age.AgeImpl;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.Annotation;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AgeImplTest {

    @Mock
    private ConstraintValidatorContext context;

    private AgeImpl ageImpl;

    @BeforeEach
    void setUp() {
        ageImpl = new AgeImpl();

        Age ageAnnotation = new Age() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return "";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int max() {
                return 100;
            }
        };

        ageImpl.initialize(ageAnnotation);
    }

    @Test
    void isValid_shouldReturnTrue_whenBirthDateIsValid() {
        LocalDate birthDate = LocalDate.now().minusYears(25);
        boolean result = ageImpl.isValid(birthDate, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_shouldReturnTrue_whenBirthDateIsExactly100YearsAgo() {
        LocalDate birthDate = LocalDate.now().minusYears(100);
        boolean result = ageImpl.isValid(birthDate, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenBirthDateIsOlderThan100Years() {
        LocalDate birthDate = LocalDate.now().minusYears(101);
        boolean result = ageImpl.isValid(birthDate, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenBirthDateIsNull() {
        boolean result = ageImpl.isValid(null, context);

        assertThat(result).isTrue();
    }
}

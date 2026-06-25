package io.recruitcrm.microservice.candidate.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EmailUtilsTest {

    @Test
    @DisplayName("normalize null input")
    void testNormalizeNull() {
        String result = EmailUtils.normalize(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("normalize empty string")
    void testNormalizeEmptyString() {
        String result = EmailUtils.normalize("");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("normalize single space")
    void testNormalizeSingleSpace() {
        String result = EmailUtils.normalize(" ");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("normalize trimmed string")
    void testNormalizeTrimmedString() {
        String result = EmailUtils.normalize("  hello@example.com  ");
        assertThat(result).isEqualTo("hello@example.com");
    }

    @Test
    @DisplayName("normalize mixed case string")
    void testNormalizeMixedCaseString() {
        String result = EmailUtils.normalize("HeLlO@ExAmPle.CoM");
        assertThat(result).isEqualTo("hello@example.com");
    }

    @Test
    @DisplayName("extractDomain null input")
    void testExtractDomainNull() {
        String result = EmailUtils.extractDomain(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractDomain empty string")
    void testExtractDomainEmptyString() {
        String result = EmailUtils.extractDomain("");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractDomain no '@'")
    void testExtractDomainNoAt() {
        String result = EmailUtils.extractDomain("hello");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractDomain valid email")
    void testExtractDomainValidEmail() {
        String result = EmailUtils.extractDomain("john@x.com");
        assertThat(result).isEqualTo("x.com");
    }

    @Test
    @DisplayName("mask null input")
    void testMaskNull() {
        assertThatThrownBy(() -> EmailUtils.mask(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email: null");
    }

    @Test
    @DisplayName("mask empty string")
    void testMaskEmptyString() {
        assertThatThrownBy(() -> EmailUtils.mask(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email: ");
    }

    @Test
    @DisplayName("mask no '@'")
    void testMaskNoAt() {
        assertThatThrownBy(() -> EmailUtils.mask("hello"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email: hello");
    }

    @Test
    @DisplayName("mask valid email")
    void testMaskValidEmail() {
        String result = EmailUtils.mask("john@x.com");
        assertThat(result).isEqualTo("j***@x.com");
    }
}
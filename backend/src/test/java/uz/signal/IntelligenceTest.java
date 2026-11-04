package uz.signal;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IntelligenceTest {
  @Test
  void detectsInventedNumbers() {
    assertThat(
            Intelligence.unsupportedNumbers(
                "The rate is 4.25%, previously 4.50%.", "Stavka 4,25%, avval 4,50%. GDP 7.2%."))
        .containsExactly("7.2");
  }

  @Test
  void sameHeadlineClustersButUnrelatedDoesNot() {
    assertThat(
            Intelligence.similarity(
                "Federal Reserve maintains policy interest rate",
                "Federal Reserve maintains policy interest rate today"))
        .isGreaterThan(.65);
    assertThat(
            Intelligence.similarity(
                "Federal Reserve maintains policy interest rate",
                "China exports grow while semiconductor investment accelerates"))
        .isLessThan(.2);
  }

  @Test
  void scoringWeightsMatchSpecification() {
    assertThat(Intelligence.score(1, .8, .5, .9, .4, .6))
        .isCloseTo(.755, org.assertj.core.data.Offset.offset(.0001));
    assertThat(Intelligence.score(2, 2, 2, 2, 2, 2))
        .isCloseTo(1, org.assertj.core.data.Offset.offset(.0001));
  }
}

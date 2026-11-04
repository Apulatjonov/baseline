package uz.signal;

public interface LanguageModelProvider {
  record Synthesis(String title, String summary, String whyItMatters, String uzbekistanImpact) {}

  Synthesis synthesize(String sourceText) throws Exception;
}

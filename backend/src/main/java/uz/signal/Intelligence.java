package uz.signal;

import java.util.*;
import java.util.regex.Pattern;

public final class Intelligence {
  private Intelligence() {}

  public static double score(
      double source, double economic, double novelty, double uz, double market, double cross) {
    return .20 * clamp(source)
        + .25 * clamp(economic)
        + .15 * clamp(novelty)
        + .20 * clamp(uz)
        + .10 * clamp(market)
        + .10 * clamp(cross);
  }

  private static double clamp(double v) {
    return Math.max(0, Math.min(1, v));
  }

  public static Set<String> tokens(String s) {
    var result = new HashSet<String>();
    for (String t : s.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
      if (t.length() > 2
          && !Set.of("the", "and", "for", "with", "from", "this", "that").contains(t))
        result.add(t);
    return result;
  }

  public static double similarity(String a, String b) {
    var x = tokens(a);
    var y = tokens(b);
    var union = new HashSet<>(x);
    union.addAll(y);
    x.retainAll(y);
    return union.isEmpty() ? 0 : (double) x.size() / union.size();
  }

  public static String category(String text) {
    String t = text.toLowerCase(Locale.ROOT);
    if (t.matches("(?s).*(interest rate|monetary|federal reserve|central bank|policy rate).*"))
      return "monetary";
    if (t.matches("(?s).*(payment|fintech|banking|remittance).*")) return "fintech";
    if (t.matches("(?s).*(artificial intelligence|semiconductor|machine learning|\\bai\\b).*"))
      return "ai";
    if (t.matches("(?s).*(oil|gold|currency|market).*")) return "markets";
    return "macro";
  }

  public static List<String> unsupportedNumbers(String input, String output) {
    var p = Pattern.compile("(?<![\\p{L}\\d])\\d+(?:[.,]\\d+)?");
    var source = new HashSet<String>();
    p.matcher(input).results().forEach(m -> source.add(m.group().replace(',', '.')));
    return p.matcher(output)
        .results()
        .map(m -> m.group().replace(',', '.'))
        .filter(n -> !source.contains(n))
        .distinct()
        .toList();
  }
}

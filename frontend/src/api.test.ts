import { describe, it, expect } from "vitest";
import { filterStories, demoEvents } from "./api";
describe("public news filters", () => {
  it("searches historical institutions", () => {
    expect(
      filterStories(demoEvents, "archive", "Federal Reserve", false).map(
        (s) => s.id,
      ),
    ).toEqual(["fed-rate"]);
  });
  it("combines category and local relevance", () => {
    expect(
      filterStories(demoEvents, "fintech", "", true).map((s) => s.id),
    ).toEqual(["payments"]);
  });
  it("filters Uzbekistan coverage", () => {
    expect(
      filterStories(demoEvents, "uzbekistan", "", false).every(
        (s) => s.uzbekistanScore >= 0.8,
      ),
    ).toBe(true);
  });
  it("returns empty results when nothing matches", () => {
    expect(filterStories(demoEvents, "today", "nonexistentxyz", false)).toEqual(
      [],
    );
  });
});

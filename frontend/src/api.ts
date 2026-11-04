import data from "./demo.json";
export type SourceLink = {
  id: string;
  name: string;
  url: string;
  primary: boolean;
};
export type Story = {
  id: string;
  title: string;
  category: string;
  summary: string;
  whyItMatters: string;
  uzbekistanImpact: string;
  countries: string;
  entities: string;
  status: string;
  confidence: string;
  importance: number;
  uzbekistanScore: number;
  publishedAt: string;
  demo: boolean;
  validationNotes: string;
  sources: SourceLink[];
  corrections?: { date: string; note: string }[];
};
export type Source = (typeof data.sources)[number] & {
  lastSuccess?: string;
  lastFailure?: string;
};
export const demoEvents: Story[] = data.events;
export const demoSources: Source[] = data.sources;
export async function api<T>(
  path: string,
  method = "GET",
  body?: unknown,
): Promise<T> {
  const response = await fetch("/api/v1" + path, {
    method,
    headers: {
      ...(body ? { "Content-Type": "application/json" } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!response.ok) {
    let message = "So‘rov bajarilmadi (" + response.status + ")";
    try {
      const json = await response.json();
      message = json.message || message;
    } catch {
      /* Non-JSON gateway error. */
    }
    throw new Error(message);
  }
  return response.json();
}
export function filterStories(
  stories: Story[],
  section: string,
  query: string,
  onlyUz: boolean,
) {
  return stories.filter(
    (s) =>
      (["today", "archive", "brief", "uzbekistan"].includes(section) ||
        s.category === section) &&
      (section !== "uzbekistan" || s.uzbekistanScore >= 0.8) &&
      (!onlyUz || s.uzbekistanScore >= 0.8) &&
      (!query ||
        (s.title + " " + s.summary + " " + s.entities + " " + s.countries)
          .toLocaleLowerCase()
          .includes(query.toLocaleLowerCase())),
  );
}

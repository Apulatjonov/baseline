import React, { useEffect, useRef, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  ArrowRight,
  ArrowUpRight,
  Search,
  X,
  Menu,
  ExternalLink,
  ChevronDown,
} from "lucide-react";
import { api, filterStories, type Story, type Source } from "./api";
import "./style.css";
const categories: Record<string, string> = {
  today: "Bosh sahifa",
  macro: "Iqtisodiyot",
  monetary: "Pul-kredit siyosati",
  markets: "Bozorlar",
  fintech: "Banklar va fintech",
  ai: "Texnologiya",
  "central-asia": "Markaziy Osiyo",
  uzbekistan: "O‘zbekiston",
  research: "Tadqiqotlar",
  archive: "Arxiv",
  sources: "Manbalar",
  methodology: "Biz haqimizda",
  brief: "Tonggi sharh",
};
const nav = [
  "today",
  "macro",
  "markets",
  "fintech",
  "ai",
  "central-asia",
  "uzbekistan",
];
const months = [
  "yanvar",
  "fevral",
  "mart",
  "aprel",
  "may",
  "iyun",
  "iyul",
  "avgust",
  "sentabr",
  "oktabr",
  "noyabr",
  "dekabr",
];
function dateLabel(value: string) {
  const d = new Date(value);
  return `${d.getUTCDate()} ${months[d.getUTCMonth()]}, ${d.getUTCFullYear()}`;
}
const safeLink = (url: string) => /^https?:\/\//i.test(url);
function App() {
  const [section, setSection] = useState(location.hash.slice(1) || "today");
  const [stories, setStories] = useState<Story[]>([]);
  const [sources, setSources] = useState<Source[]>([]);
  const [loading, setLoading] = useState(true);
  const [offline, setOffline] = useState(false);
  const [demo, setDemo] = useState(false);
  const [query, setQuery] = useState("");
  const [searchOpen, setSearchOpen] = useState(false);
  const [menu, setMenu] = useState(false);
  const [onlyUz, setOnlyUz] = useState(false);
  const [sort, setSort] = useState("importance");
  const [selected, setSelected] = useState<Story | null>(null);
  useEffect(() => {
    const change = () => {
      setSection(location.hash.slice(1) || "today");
      setQuery("");
      setOnlyUz(false);
      setMenu(false);
    };
    window.addEventListener("hashchange", change);
    return () => window.removeEventListener("hashchange", change);
  }, []);
  useEffect(() => {
    let active = true;
    Promise.all([
      api<Story[]>("/events"),
      api<Source[]>("/sources"),
      api<{ demo: boolean }>("/status"),
    ])
      .then(([e, s, status]) => {
        if (active) {
          setStories(e);
          setSources(s);
          setDemo(status.demo);
        }
      })
      .catch(() => {
        if (active) {
          setStories([]);
          setSources([]);
          setDemo(false);
          setOffline(true);
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []);
  useEffect(() => {
    const key = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        setSelected(null);
        setSearchOpen(false);
        setMenu(false);
      }
      if ((e.ctrlKey || e.metaKey) && e.key === "k") {
        e.preventDefault();
        setSearchOpen(true);
      }
    };
    window.addEventListener("keydown", key);
    return () => window.removeEventListener("keydown", key);
  }, []);
  function go(key: string) {
    location.hash = key;
    setSection(key);
    setQuery("");
    setOnlyUz(false);
    setMenu(false);
    setSearchOpen(false);
    window.scrollTo({ top: 0 });
  }
  const filtered = filterStories(stories, section, query, onlyUz).sort(
    (a, b) =>
      sort === "latest"
        ? b.publishedAt.localeCompare(a.publishedAt)
        : b.importance - a.importance,
  );
  const isHome = section === "today" && !query && !onlyUz;
  const lead = filtered[0];
  const feed = [
    "today",
    "macro",
    "monetary",
    "markets",
    "fintech",
    "ai",
    "central-asia",
    "uzbekistan",
    "research",
    "archive",
  ].includes(section);
  return (
    <>
      <header className="site-header">
        <div className="utility">
          <span>Mustaqil iqtisodiy nigoh</span>
          <div>
            <button onClick={() => go("sources")}>Manbalar</button>
            <button onClick={() => go("methodology")}>Biz haqimizda</button>
            <span className="language">O‘zbekcha</span>
          </div>
        </div>
        <div className="masthead">
          <a href="#today" className="wordmark" onClick={() => go("today")}>
            baseline
            <span />
          </a>
          <div className="masthead-note">
            Dunyo iqtisodiyoti.
            <br />
            <strong>O‘zbek tilida, aniq va tushunarli.</strong>
          </div>
          <div className="masthead-actions">
            <span>
              {dateLabel(demo ? "2026-09-30" : new Date().toISOString())}
            </span>
            <button
              className="search-trigger"
              aria-label="Qidiruvni ochish"
              onClick={() => setSearchOpen(!searchOpen)}
            >
              <Search size={19} />
              <span>Qidirish</span>
            </button>
            <button
              className="mobile-menu"
              aria-label="Menyuni ochish"
              aria-expanded={menu}
              onClick={() => setMenu(!menu)}
            >
              {menu ? <X size={22} /> : <Menu size={22} />}
            </button>
          </div>
        </div>
        <nav
          className={"main-nav " + (menu ? "is-open" : "")}
          aria-label="Asosiy bo‘limlar"
        >
          {nav.map((key) => (
            <a
              key={key}
              href={"#" + key}
              className={section === key ? "active" : ""}
              onClick={() => go(key)}
            >
              {categories[key]}
            </a>
          ))}
          <button className="nav-more" onClick={() => go("research")}>
            Tadqiqotlar <ArrowUpRight size={13} />
          </button>
        </nav>
        {searchOpen && (
          <form
            className="header-search"
            onSubmit={(e) => {
              e.preventDefault();
              setSection("archive");
            }}
          >
            <Search size={21} />
            <input
              autoFocus
              aria-label="Saytdan qidirish"
              placeholder="Mavzu, voqea yoki tashkilot nomini yozing"
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
                if (!feed) setSection("archive");
              }}
            />
            <button
              type="button"
              aria-label="Qidiruvni yopish"
              onClick={() => {
                setSearchOpen(false);
                setQuery("");
              }}
            >
              <X size={20} />
            </button>
          </form>
        )}
      </header>
      <main className="page">
        {offline && (
          <div className="demo-banner" role="alert">
            Yangiliklarni yuklab bo‘lmadi. Iltimos, birozdan so‘ng qayta urinib
            ko‘ring.
            <button onClick={() => window.location.reload()}>
              Qayta yuklash
            </button>
          </div>
        )}
        {demo && (
          <div className="demo-banner">
            <span className="edition-label">
              {offline ? "OFFLINE NAMUNA" : "NAMUNAVIY NASHR"}
            </span>
            <span>
              Bu sahifadagi materiallar namuna. Jonli yangiliklar va bozor
              narxlari emas.
            </span>
          </div>
        )}
        {feed && (
          <>
            <div className="page-title">
              <div>
                <span className="kicker">
                  {section === "today"
                    ? "GLOBAL VOQEALAR. MAHALLIY AHAMIYAT."
                    : "BASELINE / " + categories[section]?.toUpperCase()}
                </span>
                <h1>
                  {query
                    ? "Qidiruv natijalari"
                    : section === "today"
                      ? "Iqtisodiyot nigohi"
                      : categories[section]}
                </h1>
              </div>
              <a href="#brief" onClick={() => go("brief")}>
                Tonggi sharh <ArrowRight size={17} />
              </a>
            </div>
            {loading ? (
              <div className="empty">Materiallar yuklanmoqda…</div>
            ) : isHome && lead ? (
              <>
                <div className="front-page">
                  <section className="lead-story">
                    <div className="section-label">DIQQAT MARKAZIDA</div>
                    <button
                      className="headline-link"
                      onClick={() => setSelected(lead)}
                    >
                      <h2>{lead.title}</h2>
                    </button>
                    <p className="lead-summary">{lead.summary}</p>
                    <Byline story={lead} />
                    <div className="lead-context">
                      <span>NEGA MUHIM</span>
                      <p>{lead.whyItMatters}</p>
                      <button
                        className="read-more"
                        onClick={() => setSelected(lead)}
                      >
                        Tahlilni o‘qish <ArrowRight size={16} />
                      </button>
                    </div>
                  </section>
                  <section
                    className="secondary-stories"
                    aria-label="Asosiy xabarlar"
                  >
                    {filtered.slice(1, 3).map((s) => (
                      <NewsCard
                        key={s.id}
                        story={s}
                        onOpen={() => setSelected(s)}
                        compact
                      />
                    ))}
                  </section>
                  <aside className="latest">
                    <div className="section-label">QISQA SATRLAR</div>
                    {filtered.slice(3, 7).map((s, i) => (
                      <button
                        className="latest-item"
                        key={s.id}
                        onClick={() => setSelected(s)}
                      >
                        <span className="latest-number">0{i + 1}</span>
                        <div>
                          <span className="topic">
                            {categories[s.category]}
                          </span>
                          <h3>{s.title}</h3>
                        </div>
                      </button>
                    ))}
                    <a
                      className="all-link"
                      href="#archive"
                      onClick={() => go("archive")}
                    >
                      Barcha materiallar <ArrowRight size={16} />
                    </a>
                  </aside>
                </div>
                <section className="uzbekistan-section">
                  <div className="section-top">
                    <div>
                      <span className="orange-rule" />
                      <h2>O‘zbekiston uchun</h2>
                    </div>
                    <a href="#uzbekistan" onClick={() => go("uzbekistan")}>
                      Barchasi <ArrowRight size={15} />
                    </a>
                  </div>
                  <div className="impact-grid">
                    {stories
                      .filter((s) => s.uzbekistanScore >= 0.9)
                      .slice(0, 3)
                      .map((s) => (
                        <article key={s.id}>
                          <span className="topic">
                            {categories[s.category]}
                          </span>
                          <button
                            className="headline-link"
                            onClick={() => setSelected(s)}
                          >
                            <h3>{s.title}</h3>
                          </button>
                          <p>{s.uzbekistanImpact}</p>
                          <button
                            className="read-more"
                            onClick={() => setSelected(s)}
                          >
                            Batafsil <ArrowUpRight size={15} />
                          </button>
                        </article>
                      ))}
                  </div>
                </section>
              </>
            ) : null}
            <section className="archive-section">
              <div className="section-top">
                <div>
                  <span className="orange-rule" />
                  <h2>
                    {isHome
                      ? "Yana o‘qing"
                      : query
                        ? `“${query}” bo‘yicha materiallar`
                        : categories[section]}
                  </h2>
                </div>
                <span className="result-count">
                  {(isHome ? filtered.slice(3) : filtered).length} material
                </span>
              </div>
              {!isHome && (
                <div className="filters">
                  <label className="archive-search">
                    <Search size={17} />
                    <input
                      aria-label="Materiallarni qidirish"
                      placeholder="Mavzu yoki tashkilot bo‘yicha qidirish"
                      value={query}
                      onChange={(e) => setQuery(e.target.value)}
                    />
                  </label>
                  <label className="relevance-filter">
                    <input
                      type="checkbox"
                      checked={onlyUz}
                      onChange={(e) => setOnlyUz(e.target.checked)}
                    />{" "}
                    O‘zbekiston uchun
                  </label>
                  <label className="sort">
                    <select
                      aria-label="Saralash"
                      value={sort}
                      onChange={(e) => setSort(e.target.value)}
                    >
                      <option value="importance">Muhimligi bo‘yicha</option>
                      <option value="latest">Sana bo‘yicha</option>
                    </select>
                    <ChevronDown size={14} />
                  </label>
                </div>
              )}
              {filtered.length === 0 && !loading ? (
                <div className="empty">
                  <h3>Material topilmadi</h3>
                  <p>
                    Boshqa so‘z bilan qidiring yoki filtrlarni olib tashlang.
                  </p>
                  <button
                    onClick={() => {
                      setQuery("");
                      setOnlyUz(false);
                    }}
                  >
                    Filtrlarni tozalash <ArrowRight size={15} />
                  </button>
                </div>
              ) : (
                <div className="news-grid">
                  {(isHome ? filtered.slice(3) : filtered).map((s) => (
                    <NewsCard
                      key={s.id}
                      story={s}
                      onOpen={() => setSelected(s)}
                    />
                  ))}
                </div>
              )}
            </section>
          </>
        )}
        {section === "brief" && (
          <>
            <div className="page-title">
              <div>
                <span className="kicker">BASELINE / TONGGI SHARH</span>
                <h1>Bilishingiz kerak bo‘lgan 5 voqea</h1>
                <p>Kunning asosiy mavzulari va ularning ahamiyati.</p>
              </div>
            </div>
            <div className="brief-layout">
              <div>
                {stories.slice(0, 5).map((s, i) => (
                  <article className="brief-item" key={s.id}>
                    <span className="brief-number">0{i + 1}</span>
                    <div>
                      <span className="topic">{categories[s.category]}</span>
                      <button
                        className="headline-link"
                        onClick={() => setSelected(s)}
                      >
                        <h2>{s.title}</h2>
                      </button>
                      <p>{s.summary}</p>
                      <p className="brief-relevance">
                        <strong>O‘zbekiston uchun.</strong> {s.uzbekistanImpact}
                      </p>
                      <Byline story={s} />
                    </div>
                  </article>
                ))}
              </div>
              <aside className="editorial-note">
                <span className="section-label">QISQA. ANIQ. MANBALI.</span>
                <h2>
                  Kengroq manzara,
                  <br />
                  kamroq shovqin.
                </h2>
                <p>
                  Har bir materialda nima bo‘lgani, nega muhimligi va
                  O‘zbekiston uchun ehtimoliy ta’siri alohida tushuntiriladi.
                </p>
                <a href="#methodology" onClick={() => go("methodology")}>
                  Tahririy tamoyillar <ArrowRight size={16} />
                </a>
              </aside>
            </div>
          </>
        )}
        {section === "sources" && (
          <>
            <div className="page-title">
              <div>
                <span className="kicker">OCHIQ MANBALAR</span>
                <h1>Ishonch manbadan boshlanadi.</h1>
                <p>
                  Markaziy banklar, xalqaro institutlar va ixtisoslashgan
                  nashrlar.
                </p>
              </div>
            </div>
            <p className="source-intro">
              Reyestrda {sources.length} ta tashkilot,{" "}
              {sources.filter((s) => s.enabled).length} ta RSS manbasi
              sozlangan. Birlamchi manbalarga ustuvorlik beramiz. Sozlangan
              manba hali jonli tekshiruvdan o‘tmagan bo‘lishi mumkin.
            </p>
            <div className="sources-list">
              {sources.map((s) => (
                <article key={s.id}>
                  <div>
                    <span className="topic">
                      {s.sourceType === "PRIMARY"
                        ? "Birlamchi manba"
                        : "Ixtisoslashgan manba"}{" "}
                      · {s.country}
                    </span>
                    <h2>{s.name}</h2>
                  </div>
                  <a
                    href={"https://" + s.domain}
                    target="_blank"
                    rel="noreferrer"
                  >
                    {s.domain} <ExternalLink size={14} />
                  </a>
                  <span className="source-state">
                    {s.lastFailure
                      ? "Tekshirish talab etiladi"
                      : s.lastSuccess
                        ? "Tekshirilgan"
                        : s.enabled
                          ? "RSS sozlangan"
                          : "Reyestrda"}
                  </span>
                </article>
              ))}
            </div>
          </>
        )}
        {section === "methodology" && (
          <div className="about-page">
            <span className="kicker">BASELINE HAQIDA</span>
            <h1>Axborotdan — tushunchaga.</h1>
            <p className="about-lead">
              Baseline — dunyo iqtisodiyotidagi muhim voqealarni o‘zbek tilida
              tushuntiradigan ochiq nashr. Barcha materiallar erkin o‘qish uchun
              ochiq.
            </p>
            {[
              [
                "Manba birinchi o‘rinda",
                "Markaziy banklar, xalqaro tashkilotlar va statistika idoralarining birlamchi ma’lumotlariga tayanamiz. Xulosalar asl manbalarga havolalar bilan beriladi.",
              ],
              [
                "Bir voqea — bir xulosa",
                "Turli manbalardagi bir voqeaga oid xabarlar birlashtiriladi. Bir xil xabarlarni ko‘paytirish o‘rniga, voqeaning umumiy manzarasini ko‘rsatamiz.",
              ],
              [
                "Fakt va talqin alohida",
                "Nima bo‘lgani manbalar asosida bayon qilinadi. “Nega muhim” va “O‘zbekiston uchun” bo‘limlari ehtimoliy ta’sirni tushuntiradi. Noaniqliklar ochiq ko‘rsatiladi.",
              ],
              [
                "Texnologiya va inson nazorati",
                "Sun’iy intellekt manbalarni kuzatish, tarjima va xulosa tayyorlashga yordam beradi. Avtomatik tekshiruv barcha faktlarni tasdiqlamaydi. Nashr qilishdan oldin inson tekshiruvi talab etiladi.",
              ],
              [
                "Ochiq tuzatishlar",
                "Muhim o‘zgarishlar izoh bilan beriladi. Oldingi tahrirlar tarixda saqlanadi. Materiallar asl jurnalistik reportaj yoki moliyaviy maslahat sifatida taqdim etilmaydi.",
              ],
            ].map(([title, body], i) => (
              <article key={title}>
                <span>0{i + 1}</span>
                <div>
                  <h2>{title}</h2>
                  <p>{body}</p>
                </div>
              </article>
            ))}
          </div>
        )}
        {!feed && !["brief", "sources", "methodology"].includes(section) && (
          <div className="empty">
            <h1>Sahifa topilmadi</h1>
            <button onClick={() => go("today")}>
              Bosh sahifaga qaytish <ArrowRight size={16} />
            </button>
          </div>
        )}
        <div className="bottom-note">
          <strong>Muhim voqealar. Ishonchli manbalar.</strong>
          <p>
            Global iqtisodiyot, moliya va texnologiyalar — O‘zbekiston nuqtayi
            nazaridan.
          </p>
          <a href="#methodology" onClick={() => go("methodology")}>
            Qanday ishlaymiz <ArrowRight size={16} />
          </a>
        </div>
      </main>
      <footer className="site-footer">
        <div className="footer-main">
          <a className="wordmark" href="#today" onClick={() => go("today")}>
            baseline
            <span />
          </a>
          <div>
            <a href="#methodology" onClick={() => go("methodology")}>
              Biz haqimizda
            </a>
            <a href="#sources" onClick={() => go("sources")}>
              Manbalar
            </a>
            <a href="#archive" onClick={() => go("archive")}>
              Arxiv
            </a>
            <a href="#brief" onClick={() => go("brief")}>
              Tonggi sharh
            </a>
          </div>
        </div>
        <div className="footer-bottom">
          <span>
            © {new Date().getFullYear()} Baseline. Ochiq iqtisodiy nashr.
          </span>
          <span>Faktlar. Kontekst. Ahamiyat.</span>
        </div>
      </footer>
      {selected && (
        <ArticleModal story={selected} onClose={() => setSelected(null)} />
      )}
    </>
  );
}
function Byline({ story }: { story: Story }) {
  return (
    <div className="byline">
      <span>{dateLabel(story.publishedAt)}</span>
      <span className="byline-dot">·</span>
      <span>{story.sources.length} manba</span>
      {story.demo && (
        <>
          <span className="byline-dot">·</span>
          <span>Namuna</span>
        </>
      )}
    </div>
  );
}
function NewsCard({
  story: s,
  onOpen,
  compact = false,
}: {
  story: Story;
  onOpen: () => void;
  compact?: boolean;
}) {
  return (
    <article className={"news-card " + (compact ? "compact" : "")}>
      <span className="topic">{categories[s.category]}</span>
      <button className="headline-link" onClick={onOpen}>
        <h2>{s.title}</h2>
      </button>
      <p>{s.summary}</p>
      <Byline story={s} />
    </article>
  );
}
function ArticleModal({
  story: s,
  onClose,
}: {
  story: Story;
  onClose: () => void;
}) {
  const ref = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const previous = document.activeElement as HTMLElement;
    document.body.style.overflow = "hidden";
    ref.current?.focus();
    return () => {
      document.body.style.overflow = "";
      previous?.focus();
    };
  }, []);
  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div
        className="article-modal"
        role="dialog"
        aria-modal="true"
        aria-label={s.title}
        ref={ref}
        tabIndex={-1}
        onClick={(e) => e.stopPropagation()}
        onKeyDown={(e) => {
          if (e.key !== "Tab") return;
          const elements =
            ref.current?.querySelectorAll<HTMLElement>("button,a");
          if (!elements?.length) return;
          const first = elements[0],
            last = elements[elements.length - 1];
          if (
            e.shiftKey &&
            (document.activeElement === first ||
              document.activeElement === ref.current)
          ) {
            e.preventDefault();
            last.focus();
          } else if (!e.shiftKey && document.activeElement === last) {
            e.preventDefault();
            first.focus();
          }
        }}
      >
        <div className="article-top">
          <span className="wordmark small-wordmark">
            baseline
            <span />
          </span>
          <button aria-label="Yopish" onClick={onClose}>
            <X size={23} />
          </button>
        </div>
        <span className="topic">{categories[s.category]}</span>
        <h1>{s.title}</h1>
        <Byline story={s} />
        {s.demo && (
          <p className="article-demo">
            Namunaviy tahlil. Joriy yangilik emas. Havolalar tegishli
            tashkilotlarning saytlariga olib boradi.
          </p>
        )}
        <p className="article-summary">{s.summary}</p>
        <section>
          <h2>Nega muhim?</h2>
          <p>{s.whyItMatters}</p>
        </section>
        {s.uzbekistanImpact && (
          <section className="article-impact">
            <h2>O‘zbekiston uchun</h2>
            <p>{s.uzbekistanImpact}</p>
            <small>
              Bu ehtimoliy ta’sir tahlili, kafolatlangan prognoz emas.
            </small>
          </section>
        )}
        <section className="article-sources">
          <h2>Manbalar</h2>
          {s.sources
            .filter((x) => safeLink(x.url))
            .map((x) => (
              <a
                key={x.id + x.url}
                href={x.url}
                target="_blank"
                rel="noreferrer"
              >
                {x.name}
                <ExternalLink size={15} />
              </a>
            ))}
        </section>
        {s.corrections?.map((c, i) => (
          <p className="correction" key={i}>
            <strong>Tuzatish · {dateLabel(c.date)}.</strong> {c.note}
          </p>
        ))}
        <p className="article-disclaimer">
          {s.demo
            ? "Namoyish uchun tayyorlangan material."
            : "AI yordamida tayyorlangan va tahririyat tomonidan tasdiqlangan xulosa."}{" "}
          Moliyaviy maslahat emas.
        </p>
      </div>
    </div>
  );
}
createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);

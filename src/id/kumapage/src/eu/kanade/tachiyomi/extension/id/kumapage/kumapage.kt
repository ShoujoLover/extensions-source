package eu.kanade.tachiyomi.extension.id.kumapage

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class KumaPage : ParsedHttpSource() {

    override val name = "KumaPage"
    override val baseUrl = "https://kumapage.com"
    override val lang = "id"
    override val supportsLatest = true

    // Populer manga request
    override fun popularMangaRequest(page: Int) = GET("$baseUrl/daftar-komik?page=$page", headers)

    // Selector manga populer
    override fun popularMangaSelector() = "div#daftar-komik a.ui.link"

    // Mengambil manga dari elemen
    override fun popularMangaFromElement(element: Element) = SManga.create().apply {
        title = element.selectFirst("p.nama-full")?.text() ?: ""
        setUrlWithoutDomain(element.attr("href"))
        thumbnail_url = element.selectFirst("img")?.attr("src")
    }

    // Selector halaman berikutnya untuk manga populer
    override fun popularMangaNextPageSelector() = "a.nextpostslink"

    // Request manga terbaru
    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/daftar-komik?page=$page", headers)

    // Selector manga terbaru
    override fun latestUpdatesSelector() = popularMangaSelector()

    // Mengambil manga terbaru dari elemen
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)

    // Selector halaman berikutnya untuk manga terbaru
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    // Request pencarian manga
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList) =
        GET("$baseUrl/daftar-komik?page=$page&s=$query", headers)

    // Selector pencarian manga
    override fun searchMangaSelector() = popularMangaSelector()

    // Mengambil manga dari pencarian
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)

    // Selector halaman berikutnya pencarian manga
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    // Mendapatkan detail manga
    override fun mangaDetailsParse(document: Document) = SManga.create().apply {
        title = document.select("h1.entry-title").text()
        author = document.select("div.author").text()
        artist = document.select("div.artist").text()
        genre = document.select("div.genres").text()
        description = document.select("div.description").text()
        status = SManga.ONGOING
        thumbnail_url = document.select("div.thumb > img").attr("src")
    }

    // Selector daftar chapter manga
    override fun chapterListSelector() = "ul.chapters > li > a"

    // Membaca chapter dari elemen
    override fun chapterFromElement(element: Element) = SChapter.create().apply {
        name = element.text()
        setUrlWithoutDomain(element.attr("href"))
    }

    // Parsing halaman manga
    override fun pageListParse(document: Document): List<Page> {
        return document.select("div.page-break > img").mapIndexed { index, element ->
            Page(index, "", element.attr("src"))
        }
    }

    // Parsing URL gambar
    override fun imageUrlParse(document: Document) = ""
}
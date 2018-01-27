package com.example.peanutbook.fakeappka

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Menu
import android.view.MenuItem
import com.example.peanutbook.fakeappka.firebase.DatabaseRead
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import rx.android.schedulers.AndroidSchedulers
import android.content.Intent
import android.graphics.Typeface
import com.example.peanutbook.fakeappka.extensions.formatSiteName
import com.example.peanutbook.fakeappka.extensions.formatSiteRating
import com.example.peanutbook.fakeappka.logic.WebsiteHelper
import com.example.peanutbook.fakeappka.model.Website
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_table_row.view.*
import net.alhazmy13.wordcloud.ColorTemplate
import net.alhazmy13.wordcloud.WordCloud
import org.jetbrains.anko.imageResource

/**
 * Main screen
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        handleIncomingUrl()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun loadData() {
        DatabaseRead.websites()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == null) {
                        Log.d("Warning", "No websites from database were loaded")
                    } else if (it.isEmpty()) {
                        Log.d("Warning", "List of loaded websites is empty")
                    } else {
                        Log.d("Success", "Websites were loaded ( count is: ${it.count()})")
                        WebsiteHelper.setWebsites(it)
                    }
                }
    }

    private fun handleIncomingUrl() {
        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        url?.let {
            val urlWithoutPrefixAndSuffix: String = url.substringAfter("//www.").substringBefore("/").replace(".", "-")
            Log.d("URL", "PARSED URL: $urlWithoutPrefixAndSuffix")
            getWebsiteFromFirebase(parseUrl(url))
        }
    }

    private fun parseUrl(defaultUrl: String): String {
        var url = defaultUrl
        if (url.contains("http://")) {
            url = url.substringAfter("http://")
        } else if (url.contains("https://")) {
            url = url.substringAfter("https://")
        }

        url = url.substringBefore("/")
        val sequence = url.split(".").takeLast(2)
        return sequence[0] + "." + sequence[1]
    }

    private fun getWebsiteFromFirebase(websiteUrl: String) {
        val foundWebsite = WebsiteHelper.getWebsites().find { it.getId().substringBefore("_").replace("-", ".") == websiteUrl }
        if (foundWebsite == null) {
            throw RuntimeException("There is no such website in database: $websiteUrl")
        } else {
            setupUI(foundWebsite)
        }
    }

    private fun setupUI(foundWebsite: Website) {
        vProgress.visibility = View.GONE
        vFirstCard.vWebsite.text = foundWebsite.getId().formatSiteName()
        vFirstCard.vName.text = foundWebsite.houses.firstOrNull()
        vFirstCard.vDate.text = foundWebsite.established
        vFirstCard.vOwner.text = foundWebsite.people.firstOrNull()

        setupFollowers(foundWebsite.followers.toList().take(3))
        setupFollowing(foundWebsite.following.toList().take(3))
        setupTopic(foundWebsite.topic.first(), foundWebsite.getId().substringAfter("_"))
        setupWordCloud(foundWebsite.getWords())
        setupProgress()
        setupRelatedSites(foundWebsite.related_sites)
    }

    private fun setupProgress() {
        // These are just dummy data
        vProgress1.max = 100.toFloat()
        vProgress1.progress = 50.toFloat()
        vProgress1.progressColor = resources.getColor(R.color.red_negative)

        vProgress2.max = 100.toFloat()
        vProgress2.progress = 15.toFloat()
        vProgress2.progressColor = resources.getColor(R.color.red_negative)
    }

    private fun setupWordCloud(list: List<WordCloud>) {
        vWordCloud.setDataSet(list)
        vWordCloud.setSize(200, 200)
        vWordCloud.setColors(ColorTemplate.MATERIAL_COLORS)
        vWordCloud.notifyDataSetChanged()
    }

    private fun setupTopic(topic: String, rating: String) {
        vMainTopic.text = topic
        vRating.imageResource = if (rating == "nice") {
            R.drawable.ic_close
        } else {
            R.drawable.ic_iluminati
        }
    }

    private fun setupRelatedSites(relatedSites: List<String>) {
        relatedSites.forEachIndexed { index: Int, siteName: String ->
            val view = layoutInflater.inflate(R.layout.include_table_row, null)
            view.vText.text = siteName
            view.vOrder.text = index.inc().toString()
            vRelatedSitesContainer.addView(view)
        }
    }

    private fun setupFollowers(followerList: List<Pair<String, Double>>) {
        vFollowerContainer.vFirstRow.vOrder.text = getString(R.string.no)
        vFollowerContainer.vFirstRow.vText.text = getString(R.string.site)
        vFollowerContainer.vFirstRow.vIsTrustWorthyText.text = getString(R.string.fakenews)

        vFollowerContainer.vFirstRow.vOrder.typeface = Typeface.DEFAULT_BOLD
        vFollowerContainer.vFirstRow.vText.typeface = Typeface.DEFAULT_BOLD
        vFollowerContainer.vFirstRow.vIsTrustWorthyText.typeface = Typeface.DEFAULT_BOLD

        val sortedListByPower = followerList.sortedBy { it.second }

        sortedListByPower.forEachIndexed { index, network ->
            val name = network.first.replace("-", ".").substringBefore("_").replace("___TECKA___", ".")
            val rating = network.first.replace("-", ".").substringAfter("_")
            val view = layoutInflater.inflate(R.layout.include_table_row, null)
            view.vOrder.text = index.inc().toString()
            view.vText.text = name
            view.vIsTrustWorthy.imageResource = if (rating == "nice") {
                R.drawable.ic_close
            } else {
                R.drawable.ic_iluminati
            }
            vFollowerContainer.addView(view)
        }
    }

    private fun setupFollowing(followingList: List<Pair<String, Double>>) {
        vFollowingContainer.vFirstRow.vOrder.text = getString(R.string.no)
        vFollowingContainer.vFirstRow.vText.text = getString(R.string.site)
        vFollowingContainer.vFirstRow.vIsTrustWorthyText.text = getString(R.string.fakenews)

        vFollowingContainer.vFirstRow.vOrder.typeface = Typeface.DEFAULT_BOLD
        vFollowingContainer.vFirstRow.vText.typeface = Typeface.DEFAULT_BOLD
        vFollowingContainer.vFirstRow.vIsTrustWorthyText.typeface = Typeface.DEFAULT_BOLD

        val sortedListByPower = followingList.sortedBy { it.second }

        sortedListByPower.forEachIndexed { index, network ->
            val name = network.first.formatSiteName()
            val rating = network.first.formatSiteRating()
            val view = layoutInflater.inflate(R.layout.include_table_row, null)
            view.vOrder.text = index.inc().toString()
            view.vText.text = name
            view.vIsTrustWorthy.imageResource = if (rating == "nice") {
                R.drawable.ic_close
            } else {
                R.drawable.ic_iluminati
            }
            vFollowingContainer.addView(view)
        }
    }
}
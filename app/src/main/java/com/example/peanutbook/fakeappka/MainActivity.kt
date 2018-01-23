package com.example.peanutbook.fakeappka

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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
import android.provider.ContactsContract
import com.example.peanutbook.fakeappka.logic.WebsiteHelper
import com.example.peanutbook.fakeappka.model.Network
import com.example.peanutbook.fakeappka.model.Website
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_table_row.view.*
import net.alhazmy13.wordcloud.ColorTemplate
import net.alhazmy13.wordcloud.WordCloud
import org.apache.commons.lang3.text.translate.NumericEntityEscaper
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.textColor
import java.util.*


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
        handleIncomingUrl()
        loadData()

    }

    fun loadData() {
        DatabaseRead.websites()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it == null) {
                    Log.d("Warning", "No websites from database were loaded")
                } else if (it.isEmpty()) {
                    Log.d("Warning", "List of loaded websites is empty")
                } else {
                    Log.d("Succes", "WEBSITES were loaded ( count is: ${it.count()})")
                    WebsiteHelper.setWebsites(it)
                }
            }
    }

    fun handleIncomingUrl() {
        // Get intent, action and MIME type
        val intent = intent
        val action = intent.action
        val type = intent.type

        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        url?.let {
            val urlWithoutPrefixAndSuffix: String = url.substringAfter("//www.").substringBefore("/").replace(".","-")
            Log.d("URL", "PARSED URL: $urlWithoutPrefixAndSuffix")
            getWebsiteFromFirebase(parseUrl(url))
        }
    }

    fun parseUrl(defaultUrl: String): String {
        var url = defaultUrl
        if (url.contains("http://")) {
            url = url.substringAfter("http://")
        } else if (url.contains("https://")) {
            url = url.substringAfter("https://")
        }

        url = url.substringBefore("/")
        val sequence = url.split(".").takeLast(2)
        val mergedSequence = sequence[0] + "." + sequence[1]
        return mergedSequence

    }

    fun getWebsiteFromFirebase(websiteUrl: String) {
        val findedWebsite = WebsiteHelper.getWebsites().find { it.getId().substringBefore("_").replace("-",".") == websiteUrl }
        if (findedWebsite == null) {
            throw RuntimeException("There is no such website in database: $websiteUrl")
        } else {
            setupUI(findedWebsite)
        }
    }

    private fun setupUI(findedWebsite: Website) {
        vProgress.visibility = View.GONE
        vFirstCard.vWebsite.text = findedWebsite.getId().substringBefore("_").replace("-",".")
        vFirstCard.vName.text = findedWebsite.houses.firstOrNull()
        vFirstCard.vDate.text = findedWebsite.established
        vFirstCard.vOwner.text = findedWebsite.people.firstOrNull()

        /*setupTable(findedWebsite.network.toList())*/
        setupFollowers(findedWebsite.followers.toList().take(3))
        setupFollowing(findedWebsite.following.toList().take(3))
        setupTopic(findedWebsite.topic.first(), findedWebsite.getId().substringAfter("_"))
        setupWordCloud(findedWebsite.getWords())
        setupProgress()
        setupRelatedSites(findedWebsite.related_sites)
    }

    private fun setupProgress() {
        vProgress1.setMax(100.toFloat());
        vProgress1.setProgress(50.toFloat());
        vProgress1.progressColor = resources.getColor(R.color.red_negative)


        vProgress2.setMax(100.toFloat());
        vProgress2.setProgress(15.toFloat());
        vProgress2.progressColor = resources.getColor(R.color.red_negative)
    }

    private fun setupWordCloud(list: List<WordCloud>) {
        vWordCloud.setDataSet(list)
        vWordCloud.setSize(200,200);
        vWordCloud.setColors(ColorTemplate.MATERIAL_COLORS);
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

            val name = siteName.replace("-",".").substringBefore("_").replace("___TECKA___",".")
            view.vText.text = name
            view.vOrder.text = index.inc().toString()
            vRelatedSitesContainer.addView(view)
        }
    }

    private fun setupFollowers(followerList: List<Pair<String, Double>>) {
        vFollowerContainer.vFirstRow.vOrder.text = "No."
        vFollowerContainer.vFirstRow.vText.text = "Site"
        vFollowerContainer.vFirstRow.vIsTrustWorthyText.text = "Fakenews"

        vFollowerContainer.vFirstRow.vOrder.typeface = Typeface.DEFAULT_BOLD
        vFollowerContainer.vFirstRow.vText.typeface = Typeface.DEFAULT_BOLD
        vFollowerContainer.vFirstRow.vIsTrustWorthyText.typeface = Typeface.DEFAULT_BOLD

       val sortedListByPower = followerList.sortedBy { it.second }

        sortedListByPower.forEachIndexed { index, network ->
            val name = network.first.replace("-",".").substringBefore("_").replace("___TECKA___",".")
            val rating = network.first.replace("-",".").substringAfter("_")
            val view = layoutInflater.inflate(R.layout.include_table_row, null)
            view.vOrder.text = index.inc().toString()
            view.vText.text = name
        //    view.vIsTrustWorthyText.text = network.second.toString()
            view.vIsTrustWorthy.imageResource = if (rating == "nice") {
                R.drawable.ic_close
            } else {
                R.drawable.ic_iluminati
            }

            vFollowerContainer.addView(view)

        }
    }

    private fun setupFollowing(followingList: List<Pair<String, Double>>) {
        vFollowingContainer.vFirstRow.vOrder.text = "No."
        vFollowingContainer.vFirstRow.vText.text = "Site"
        vFollowingContainer.vFirstRow.vIsTrustWorthyText.text = "Fakenews"

        vFollowingContainer.vFirstRow.vOrder.typeface = Typeface.DEFAULT_BOLD
        vFollowingContainer.vFirstRow.vText.typeface = Typeface.DEFAULT_BOLD
        vFollowingContainer.vFirstRow.vIsTrustWorthyText.typeface = Typeface.DEFAULT_BOLD

        val sortedListByPower = followingList.sortedBy { it.second }

        sortedListByPower.forEachIndexed { index, network ->
            val name = network.first.replace("-",".").substringBefore("_").replace("___TECKA___",".")
            val rating = network.first.replace("-",".").substringAfter("_")
            val view = layoutInflater.inflate(R.layout.include_table_row, null)
            view.vOrder.text = index.inc().toString()
            view.vText.text = name
  //          view.vIsTrustWorthyText.text = network.second.toString()
            view.vIsTrustWorthy.imageResource = if (rating == "nice") {
                R.drawable.ic_close
            } else {
                R.drawable.ic_iluminati
            }
            vFollowingContainer.addView(view)
        }
    }

    private fun setupTable(networkList: List<Pair<String,Network>>) {
        vTableContainer.vFirstRow.vOrder.text = "No."
        vTableContainer.vFirstRow.vText.text = "Source"
        vTableContainer.vFirstRow.vIsTrustWorthyText.text = "Fakenews"

        vTableContainer.vFirstRow.vOrder.typeface = Typeface.DEFAULT_BOLD
        vTableContainer.vFirstRow.vText.typeface = Typeface.DEFAULT_BOLD
        vTableContainer.vFirstRow.vIsTrustWorthyText.typeface = Typeface.DEFAULT_BOLD

        networkList.forEachIndexed { index, network ->
            val view = layoutInflater.inflate(R.layout.include_table_row, null)
            view.vOrder.text = index.inc().toString()
            view.vText.text = network.second.source.replace("-",".")
            view.vIsTrustWorthy.imageResource = if (network.second.isKonspirator == "true") {
                R.drawable.ic_close
            } else {
                R.drawable.ic_check
            }
            vTableContainer.addView(view)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun initUi() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}

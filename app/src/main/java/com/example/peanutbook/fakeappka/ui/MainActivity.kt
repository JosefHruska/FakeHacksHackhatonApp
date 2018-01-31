package com.example.peanutbook.fakeappka.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.peanutbook.fakeappka.R
import com.example.peanutbook.fakeappka.extensions.formatSiteName
import com.example.peanutbook.fakeappka.extensions.formatSiteRating
import com.example.peanutbook.fakeappka.extensions.parseUrl
import com.example.peanutbook.fakeappka.model.Website
import com.example.peanutbook.fakeappka.ui.about.AboutActivity
import com.example.peanutbook.fakeappka.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_idle.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.include_table_row.view.*
import net.alhazmy13.wordcloud.ColorTemplate
import net.alhazmy13.wordcloud.WordCloud
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.startActivity

/**
 * Main screen
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainActivity : BaseActivity<MainViewModel, MainController>(), MainController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingUrl()
        setupSelectedWebsite()
        setupCreditsText()
    }

    override fun setupToolbar() {
        super.setupToolbar()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun getOptionsMenuRes() = R.menu.menu_main

    override fun getToolbarTitle() = R.string.app_name

    override fun getViewModel() = MainViewModel()

    override fun getLayoutRes() = R.layout.activity_main

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_about) {
            startActivity<AboutActivity>()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun handleIncomingUrl() {
        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        url?.let {
            mViewModel.websiteSelected(url.parseUrl())
        }
    }

    private fun setupUI(foundWebsite: Website) {
        vContentIdle.visibility = View.GONE
        vContentMain.visibility = View.VISIBLE

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

    private fun setupSelectedWebsite() {
        val groupsObserver = Observer<Website> {
            it?.let { setupUI(it) }
        }
        mViewModel.getSelectedWebsite().observe(this, groupsObserver)
    }

    private fun setupCreditsText() {
        vCredits.setOnClickListener { startActivity<AboutActivity>() }
    }

    private fun setupProgress() {
        // These are just dummy data - we didn't have enough data to do precise analyze of sharing.
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
        if (relatedSites.isEmpty()) {
            vRelatedSitesCard.visibility = View.GONE
        } else {
            vRelatedSitesCard.visibility = View.VISIBLE
            relatedSites.forEachIndexed { index: Int, siteName: String ->
                val view = layoutInflater.inflate(R.layout.include_table_row, null)
                view.vText.text = siteName
                view.vOrder.text = index.inc().toString()
                vRelatedSitesContainer.addView(view)
            }
        }
    }

    private fun setupFollowers(followerList: List<Pair<String, Double>>) {
        if (followerList.isEmpty()) {
            vFollowerTable.visibility = View.GONE
        } else {
            vFollowerTable.visibility = View.VISIBLE
            vFollowerContainer.setupFirstRow()
            val sortedListByPower = followerList.sortedBy { it.second }
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
                vFollowerContainer.addView(view)
            }
        }
    }

    private fun setupFollowing(followingList: List<Pair<String, Double>>) {
        vFollowingContainer.setupFirstRow()

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

    private fun View.setupFirstRow() {
        vFirstRow.vOrder.text = getString(R.string.no)
        vFirstRow.vText.text = getString(R.string.site)
        vFirstRow.vIsTrustWorthyText.text = getString(R.string.fakenews)

        vFirstRow.vOrder.typeface = Typeface.DEFAULT_BOLD
        vFirstRow.vText.typeface = Typeface.DEFAULT_BOLD
        vFirstRow.vIsTrustWorthyText.typeface = Typeface.DEFAULT_BOLD
    }
}
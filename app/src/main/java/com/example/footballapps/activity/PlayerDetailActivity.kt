package com.example.footballapps.activity

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.PlayerItem
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.presenter.PlayerDetailPresenter
import com.example.footballapps.repository.PlayerDetailRepository
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.PlayerDetailView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_player_detail.*
import kotlinx.android.synthetic.main.layout_player_detail_description.*
import kotlinx.android.synthetic.main.layout_player_detail_biodata.*
import kotlinx.android.synthetic.main.layout_player_detail_title.*
import kotlinx.android.synthetic.main.layout_player_detail_physique.*
import kotlinx.android.synthetic.main.layout_player_detail_team_info.*
import java.text.SimpleDateFormat
import java.util.*
import java.lang.StringBuilder

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PlayerDetailActivity : AppCompatActivity(), PlayerDetailView {

    private lateinit var playerItem: PlayerItem

    private lateinit var playerDetailPresenter: PlayerDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        initData()
    }

    private fun initData() {
        val intent = intent
        playerItem = intent.getParcelableExtra("playerItem")

        Glide.with(applicationContext)
            .load(playerItem.playerFanArt)
            .placeholder(R.drawable.ic_player_picture_placeholder)
            .into(iv_player_fanart)

        setToolbarBehavior()

        playerDetailPresenter = PlayerDetailPresenter(this, PlayerDetailRepository())

        EspressoIdlingResource.increment()
        playerDetailPresenter.getPlayerDetailInfo(playerItem.playerId!!)

        player_detail_swipe_refresh_layout.setOnRefreshListener {
            EspressoIdlingResource.increment()
            playerDetailPresenter.getPlayerDetailInfo(playerItem.playerId!!)
        }

        player_detail_swipe_refresh_layout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.colorAccent
            )
        )


    }

    private fun setToolbarBehavior() {
        setSupportActionBar(toolbar_detail_player)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = playerItem.playerName
    }

    override fun dataIsLoading() {
        progress_bar_player_detail.visible()
        player_detail_error_data_text.gone()
        layout_player_detail_data.invisible()
    }

    override fun dataLoadingFinished() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        player_detail_swipe_refresh_layout.isRefreshing = false
        progress_bar_player_detail.gone()
        player_detail_error_data_text.gone()
        layout_player_detail_data.visible()
    }

    override fun dataFailedToLoad() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        player_detail_swipe_refresh_layout.isRefreshing = false
        progress_bar_player_detail.gone()
        player_detail_error_data_text.visible()
        layout_player_detail_data.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            player_detail_error_data_text.text = getString(R.string.no_data_to_show)
        } else {
            player_detail_error_data_text.text = getString(R.string.no_internet_connection)
        }

    }

    override fun showPlayerDetailData(playerResponse: PlayerResponse) {
        val playerDetailList = playerResponse.playerDetail
        val playerDetailItem = playerDetailList?.first()

        Glide.with(applicationContext)
            .load(playerDetailItem?.playerPhoto)
            .placeholder(R.drawable.ic_player_picture_placeholder)
            .into(player_detail_photo)

        player_detail_name.text = StringBuilder(formatValue(playerDetailItem?.playerName))

        player_detail_height.text = formatValue(playerDetailItem?.playerHeight)
        player_detail_weight.text = formatValue(playerDetailItem?.playerWeight)

        val dateOfBirth = formatDate(playerDetailItem?.playerBirthDate)

        val birthLocation = formatValue(playerDetailItem?.playerBirthLocation)

        tv_player_detail_date_of_birth.text = StringBuilder("Place, Date of Birth : $birthLocation, $dateOfBirth")

        tv_player_detail_nationality.text = StringBuilder("Nationality : ${formatValue(playerDetailItem?.playerNationality)}")

        tv_player_detail_outfitter.text =  StringBuilder("Outfitter : ${formatValue(playerDetailItem?.playerOutfitter)}")

        tv_player_detail_kit.text = StringBuilder("Kit : ${formatValue(playerDetailItem?.playerKit)}")

        tv_player_detail_agent.text = StringBuilder("Agent : ${formatValue(playerDetailItem?.playerAgent)}")

        tv_player_detail_position.text = StringBuilder("Position : ${formatValue(playerDetailItem?.playerPosition)}")

        tv_player_detail_preferred_foot.text = StringBuilder("Preferred Foot : ${formatValue(playerDetailItem?.playerStrongFoot)}")

        tv_player_detail_team_name.text = StringBuilder("Team Name : ${formatValue(playerDetailItem?.playerTeam)}")

        tv_player_detail_team_date_signed.text = StringBuilder("Date Signed : ${formatDate(playerDetailItem?.playerSignedDate)}")

        tv_player_detail_team_transfer_fee.text = StringBuilder("Transfer Fee : ${formatValue(playerDetailItem?.playerTransferFee)}")

        tv_player_detail_team_wage.text = StringBuilder("Wage : ${formatValue(playerDetailItem?.playerWages)}")

        tv_player_detail_team_number.text = StringBuilder("Shirt Number : ${formatValue(playerDetailItem?.playerShirtNumber)}")

        tv_player_detail_description.text = formatValue(playerDetailItem?.playerDescription)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(stringValue: String?): String {
        return if (stringValue != null && stringValue.isNotEmpty()) {
            val oldDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDateFormat = SimpleDateFormat("dd MMM yyyy")
            val date: Date = oldDateFormat.parse(stringValue)
            val formattedDateEvent = newDateFormat.format(date)

            formattedDateEvent
        } else {
            resources.getString(R.string.value_unknown)
        }
    }

    private fun formatValue(stringValue : String?) : String {
        return if(stringValue != null && stringValue.trim().isNotEmpty()) {
            stringValue
        } else {
            resources.getString(R.string.value_unknown)
        }
    }

    @Suppress("DEPRECATION")
    private fun checkNetworkConnection(): Boolean {

        val connectivityManager: ConnectivityManager? =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

                if (networkInfo != null) {
                    return (networkInfo.isConnected && (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE || networkInfo.type == ConnectivityManager.TYPE_VPN))
                }

            } else {
                val network: Network? = connectivityManager.activeNetwork

                if (network != null) {
                    val networkCapabilities: NetworkCapabilities =
                        connectivityManager.getNetworkCapabilities(network)!!


                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
                }
            }
        }
        return false
    }

}

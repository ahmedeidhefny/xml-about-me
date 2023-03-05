package xml.about.me.core.api

import xml.about.me.BuildConfig
import javax.inject.Inject

@Suppress("PrivatePropertyName", "PropertyName")
class ApiUrlHelper @Inject constructor() {

    private lateinit var API_URL: String

    lateinit var GET_ARTICLE_LIST_API_URL: String


    init {
        update()
    }


    private fun update() {
        API_URL = BuildConfig.API_URL
        GET_ARTICLE_LIST_API_URL = "${API_URL}everything"
    }
}

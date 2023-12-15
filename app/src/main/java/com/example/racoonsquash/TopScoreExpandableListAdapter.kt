package com.example.racoonsquash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

//Detta ar en custom adapter.En "custom adapter" i Android-utveckling är en adapter som du själv
// skapar och anpassar för att passa specifika behov och dataformat för din applikation
class TopScoreExpandableListAdapter(
    private val context: Context,
    private val gameList: List<String>,
    private val scoresPerGame: HashMap<String, List<DataManager.Score>>,
) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): DataManager.Score {
        return this.scoresPerGame[this.gameList[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup,
    ): View {
        var convertView = convertView
        val score = getChild(groupPosition, childPosition)

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.list_item, parent, false)
        }

//        if (convertView == null) {
//            val infalInflater = this.context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            convertView = infalInflater.inflate(R.layout.list_item, null)
//        }

        val scoreListItemTextView = convertView!!.findViewById<TextView>(R.id.scoreListItem)
        scoreListItemTextView.text = score.score.toString()

        val nameListItemTextView = convertView!!.findViewById<TextView>(R.id.nameListItem)
        nameListItemTextView.text = score.userName

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.scoresPerGame[this.gameList[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.gameList[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.gameList.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup,
    ): View {
        var convertView = convertView
        val headerTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val infalInflater = this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.list_group, null)
        }

        val lblListHeader = convertView!!.findViewById<TextView>(R.id.gameListHeader)
        lblListHeader.text = headerTitle

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}

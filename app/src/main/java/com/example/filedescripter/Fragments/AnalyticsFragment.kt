package com.example.filedescripter.Fragments

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.androidplot.pie.PieChart
import com.androidplot.pie.Segment
import com.androidplot.pie.SegmentFormatter
import com.example.filedescripter.ViewModels.AnalyticsFragmentVM
import com.example.filedescripter.R
import com.example.filedescripter.databinding.FragmentAnalyticsBinding

class AnalyticsFragment : Fragment() {
    private lateinit var _binding: FragmentAnalyticsBinding
    private lateinit var _viewModel: AnalyticsFragmentVM
    private val colorList = listOf(Color.RED, Color.GREEN, Color.DKGRAY, Color.BLUE, Color.MAGENTA, Color.GRAY)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "Anchal: onCreateView: ")
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        ViewModelProviders.of(this, AnalyticsFragmentVM.factory)[AnalyticsFragmentVM::class.java]
            .also { _viewModel = it }
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Anchal: AnalyticsFragment: onViewCreated: ")
        val mapping = _viewModel.getTypeToSizeMapping()
        updatePieChart(mapping, view)
    }

    private fun updatePieChart(mapping: Map<String, Long>, view: View) {
        var i = 0
        Log.d(TAG, "Anchal: updatePieChart: $mapping")
        var totalSize : Long = 0
        for ((key, value) in mapping) {
            totalSize += value
        }
        for ((key, value) in mapping) {
            Log.d(TAG, "Anchal: updatePieChart: $key, $value")
            val segmentPair = Segment(key + " ${value * 100 / totalSize}%", value)
            val segmentColor = SegmentFormatter(colorList[i])
            val pieChart = view.findViewById<PieChart>(R.id.pieChart)
            pieChart.addSegment(segmentPair, segmentColor)
            i++
            i %= colorList.size
        }
    }
}
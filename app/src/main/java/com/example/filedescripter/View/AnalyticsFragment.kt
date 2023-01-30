package com.example.filedescripter.View

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.androidplot.pie.PieChart
import com.androidplot.pie.Segment
import com.androidplot.pie.SegmentFormatter
import com.example.filedescripter.PathStackTracker
import com.example.filedescripter.ViewModel.AnalyticsFragmentVM
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
        val mapping = _viewModel.getTypeToSizeMapping(PathStackTracker.curPath)
        updatePieChart(mapping, view)
    }

    private fun updatePieChart(mapping: Map<String, Long>, view: View) {
        var i = 0
        Log.d(TAG, "Anchal: updatePieChart: $mapping")
        var totalSize : Long = 0
        if (mapping.isEmpty()) {
            val textView = view.findViewById<TextView>(R.id.textView)
            textView.text = "Empty Folder !!"
            return
        }
        for ((key, value) in mapping) {
            totalSize += value
        }
        if (totalSize == (0).toLong()) {
            val textView = view.findViewById<TextView>(R.id.textView)
            textView.text = "No memory used !!"
            return
        }
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        for ((key, value) in mapping) {
            val percentage = if (totalSize == (0).toLong()) 100 else value * 100 / (totalSize)
            var segmentPair = Segment("$key $percentage%", value)
            Log.d(TAG, "Anchal: updatePieChart: $key, $value $percentage%")
            if (percentage < 1) {
                Log.d(TAG, "Anchal: updatePieChart: less than 1")
                segmentPair = Segment("", value)
            }
            val segmentColor = SegmentFormatter(colorList[i])
            pieChart.addSegment(segmentPair, segmentColor)
            i++
            i %= colorList.size
        }
    }
}
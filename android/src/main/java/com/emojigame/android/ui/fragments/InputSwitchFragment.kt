package com.emojigame.android.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.emojigame.android.activities.LevelActivity
import com.emojigame.android.databinding.FragmentInputSwitchBinding
import com.emojigame.android.util.DeviceSizeData
import com.emojigame.android.util.InputType
import com.emojigame.android.util.PaddingUtil
import com.emojigame.core.ui.Theme
import kotlin.math.sqrt

class InputSwitchFragment : Fragment() {
    private var _binding: FragmentInputSwitchBinding? = null
    private val binding get() = _binding!!
    private lateinit var iconContainer: LinearLayout
    private var currentOrientation: Int = Configuration.ORIENTATION_PORTRAIT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputSwitchBinding.inflate(inflater, container, false)
        binding.theme = Theme
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentOrientation = resources.configuration.orientation
        iconContainer = binding.iconContainer
        PaddingUtil.applyUniformPaddingToChildren(iconContainer)
        setupIconDisplay(view)

        if (iconContainer.orientation == LinearLayout.HORIZONTAL)
            PaddingUtil.applyPaddingToView(
                view,
                touchingLeft = true,
                touchingTop = false,
                touchingRight = true,
                touchingBottom = false,
                ignoreTop = true,
                ignoreBottom = true
            )
        if (iconContainer.orientation == LinearLayout.VERTICAL)
            PaddingUtil.applyPaddingToView(
                view,
                touchingLeft = false,
                touchingTop = true,
                touchingRight = false,
                touchingBottom = true,
                ignoreLeft = true,
                ignoreRight = true
            )

        binding.loopIcon.setOnClickListener {
            (activity as? LevelActivity)?.changeInputFragment(InputType.LOOP)
        }

        binding.sequenceIcon.setOnClickListener {
            (activity as? LevelActivity)?.changeInputFragment(InputType.SEQUENCE)
        }

        binding.ifElseIcon.setOnClickListener {
            (activity as? LevelActivity)?.changeInputFragment(InputType.IFELSE)
        }
    }

    private fun setupIconDisplay(view: View) {
        val iconDiameter: Int

        // Move the icons a little so they merge into the input fragment
        val iconOffset = 0.1f
        // Padding so the icon (square) does not exceed the background (circle)
        val iconCirclePadding = ((1 - (sqrt(2f) / 2)) / 2) * 1.1

        when (iconContainer.orientation) {
            LinearLayout.HORIZONTAL -> {
                iconDiameter = DeviceSizeData.portraitInputSwitchIconDiameter
                view.translationY = iconDiameter * iconOffset
            }
            LinearLayout.VERTICAL -> {
                iconDiameter = DeviceSizeData.landscapeInputSwitchIconDiameter
                view.translationX = iconDiameter * iconOffset
            }
            else -> iconDiameter = DeviceSizeData.portraitInputSwitchIconDiameter
        }

        updateIconLayout(
            iconDiameter,
            iconDiameter,
            (iconDiameter * iconCirclePadding).toInt()
        )
    }

    private fun updateIconLayout(width: Int, height: Int, padding: Int) {
        for (i in 0 until iconContainer.childCount) {
            val icon = iconContainer.getChildAt(i)
            val layoutParams = icon.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.width = width
            layoutParams.height = height
            icon.setPadding(padding, padding, padding, padding)
        }
    }
}

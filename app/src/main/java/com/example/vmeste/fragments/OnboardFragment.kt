package com.example.vmeste.fragments

import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vmeste.R
import com.example.vmeste.databinding.FragmentOnboardBinding
import com.google.android.material.button.MaterialButton
import kotlin.math.sqrt
import kotlin.random.Random

class OnboardFragment : Fragment() {

    private var _binding: FragmentOnboardBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private lateinit var circles: List<TextView>
    private var animator: ValueAnimator? = null

    private val dx = FloatArray(23) { Random.nextFloat() * 8f - 4f }
    private val dy = FloatArray(23) { Random.nextFloat() * 8f - 4f }
    private val masses = FloatArray(23)

    private val circleSizes = FloatArray(23)
    private val circleRadii = FloatArray(23)
    private val centerPositions = Array(23) { FloatArray(2) }

    companion object {
        const val TAG = "OnboardFragment"
        private const val TOUCH_FORCE = 3.5f
        private const val TOUCH_RADIUS = 300f
        private const val BASE_MASS = 0.45f
        private const val DAMPING_BASE = 0.997f
        private const val COLLISION_ELASTICITY = 0.90f
        private const val MIN_VELOCITY = 0.6f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardBinding.inflate(inflater, container, false)
        setupViews(binding.root)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animator?.cancel()
        animator = null
        _binding = null
    }

    private fun setupViews(view: View) {
        binding.circles.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener { it.performClick() }
            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        handleTouch(event.x, event.y)
                        true
                    }
                    else -> false
                }
            }
        }

        circles = (1..23).map {
            view.findViewById<TextView>(
                resources.getIdentifier("circle$it", "id", requireContext().packageName)
            )
        }

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.action_onboardFragment_to_signInFragment)
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                initializeCircleProperties()
                ensureInitialVelocities()
                startAnimation()
            }
        })
    }

    private fun ensureInitialVelocities() {
        for (i in dx.indices) {
            val speed = sqrt(dx[i] * dx[i] + dy[i] * dy[i])
            if (speed < MIN_VELOCITY) {
                val angle = Random.nextDouble() * 2 * Math.PI
                dx[i] = (MIN_VELOCITY * kotlin.math.cos(angle)).toFloat() * 2
                dy[i] = (MIN_VELOCITY * kotlin.math.sin(angle)).toFloat() * 2
            }
        }
    }

    private fun initializeCircleProperties() {
        val minWidth = circles.minOf { it.width }.toFloat()
        circles.forEachIndexed { index, circle ->
            circleSizes[index] = circle.width.toFloat()
            circleRadii[index] = circle.width / 2f
            masses[index] = BASE_MASS * (1 + 0.5f * (circle.width / minWidth - 1))
            circle.x += Random.nextFloat() * 10 - 5
            circle.y += Random.nextFloat() * 10 - 5
        }
        updateCenterPositions()
    }

    private fun updateCenterPositions() {
        circles.forEachIndexed { index, circle ->
            centerPositions[index][0] = circle.x + circleRadii[index]
            centerPositions[index][1] = circle.y + circleRadii[index]
        }
    }

    private fun handleTouch(touchX: Float, touchY: Float) {
        updateCenterPositions()
        circles.forEachIndexed { i, _ ->
            val distanceX = centerPositions[i][0] - touchX
            val distanceY = centerPositions[i][1] - touchY
            val distance = sqrt(distanceX * distanceX + distanceY * distanceY)

            if (distance < TOUCH_RADIUS) {
                val force = (1 - distance / TOUCH_RADIUS) * TOUCH_FORCE
                val adjustedForce = force / sqrt(masses[i])

                val normalizedX = distanceX / distance
                val normalizedY = distanceY / distance

                dx[i] += normalizedX * adjustedForce
                dy[i] += normalizedY * adjustedForce
            }
        }
    }

    private fun moveBubble(view: View, index: Int) {
        val layoutBounds = Rect().apply { binding.circles.getDrawingRect(this) }

        val velocityX = dx[index] / sqrt(masses[index])
        val velocityY = dy[index] / sqrt(masses[index])

        if (sqrt(velocityX * velocityX + velocityY * velocityY) < MIN_VELOCITY) {
            dx[index] += Random.nextFloat() * 0.2f - 0.1f
            dy[index] += Random.nextFloat() * 0.2f - 0.1f
        }

        val newX = view.x + velocityX
        val newY = view.y + velocityY

        if (newX < 0 || newX + circleSizes[index] > layoutBounds.width()) {
            dx[index] = -dx[index] * COLLISION_ELASTICITY * (1 + Random.nextFloat() * 0.1f)
            if (newX < 0) view.x = 0f
            if (newX + circleSizes[index] > layoutBounds.width())
                view.x = layoutBounds.width() - circleSizes[index]
        } else {
            view.x = newX
        }

        if (newY < 0 || newY + circleSizes[index] > layoutBounds.height()) {
            dy[index] = -dy[index] * COLLISION_ELASTICITY * (1 + Random.nextFloat() * 0.1f)
            if (newY < 0) view.y = 0f
            if (newY + circleSizes[index] > layoutBounds.height())
                view.y = layoutBounds.height() - circleSizes[index]
        } else {
            view.y = newY
        }

        val dampingFactor = DAMPING_BASE + (1f - 1f / masses[index]) * 0.002f
        dx[index] *= dampingFactor
        dy[index] *= dampingFactor
    }

    private fun resolveCollision(i: Int, j: Int) {
        val distanceX = centerPositions[j][0] - centerPositions[i][0]
        val distanceY = centerPositions[j][1] - centerPositions[i][1]
        val distance = sqrt(distanceX * distanceX + distanceY * distanceY)

        if (distance == 0f) return

        val normalX = distanceX / distance
        val normalY = distanceY / distance

        val massSum = masses[i] + masses[j]
        val massDiff = masses[i] - masses[j]

        val velXi = dx[i]
        val velYi = dy[i]
        val velXj = dx[j]
        val velYj = dy[j]

        val randomFactor = 1f + (Random.nextFloat() * 0.1f - 0.05f)

        dx[i] = ((massDiff * velXi + 2 * masses[j] * velXj) / massSum) * COLLISION_ELASTICITY * randomFactor
        dy[i] = ((massDiff * velYi + 2 * masses[j] * velYj) / massSum) * COLLISION_ELASTICITY * randomFactor
        dx[j] = ((-massDiff * velXj + 2 * masses[i] * velXi) / massSum) * COLLISION_ELASTICITY * randomFactor
        dy[j] = ((-massDiff * velYj + 2 * masses[i] * velYi) / massSum) * COLLISION_ELASTICITY * randomFactor

        val overlap = circleRadii[i] + circleRadii[j] - distance
        if (overlap > 0) {
            val separationX = normalX * overlap * 0.5f
            val separationY = normalY * overlap * 0.5f

            circles[i].x -= separationX
            circles[j].x += separationX
            circles[i].y -= separationY
            circles[j].y += separationY
        }
    }

    private fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                updateCenterPositions()
                circles.forEachIndexed { i, circle -> moveBubble(circle, i) }
                checkCollisions()
            }
        }.also { it.start() }
    }

    private fun checkCollisions() {
        for (i in circles.indices) {
            for (j in i + 1 until circles.size) {
                val distanceX = centerPositions[j][0] - centerPositions[i][0]
                val distanceY = centerPositions[j][1] - centerPositions[i][1]
                val distance = sqrt(distanceX * distanceX + distanceY * distanceY)

                if (distance < circleRadii[i] + circleRadii[j]) {
                    resolveCollision(i, j)
                }
            }
        }
    }
}
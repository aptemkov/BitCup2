import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateInterpolator


class RevealAnimation(private val mView: View, intent: Intent, private val mActivity: Activity) {
    private var revealX = 0
    private var revealY = 0

    init {

        if (intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)
        ) {
            mView.visibility = View.INVISIBLE
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)
            val viewTreeObserver = mView.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivity(revealX, revealY)
                        mView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {
            mView.visibility = View.VISIBLE
        }
    }

    fun revealActivity(x: Int, y: Int) {
        val finalRadius = (Math.max(mView.width, mView.height) * 1.1).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal =
            ViewAnimationUtils.createCircularReveal(mView, x, y, 0f, finalRadius)
        circularReveal.duration = 300
        circularReveal.interpolator = AccelerateInterpolator()

        // make the view visible and start the animation
        mView.visibility = View.VISIBLE
        circularReveal.start()
    }

    fun unRevealActivity() {
        val finalRadius = (Math.max(mView.width, mView.height) * 1.1).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            mView, revealX, revealY, finalRadius, 0f
        )
        circularReveal.duration = 300
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mView.visibility = View.INVISIBLE
                mActivity.finish()
                mActivity.overridePendingTransition(0, 0)
            }
        })
        circularReveal.start()
    }

    companion object {
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    }
}
package com.example.bwise


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.user_edit_text),
                childAtPosition(
                    allOf(
                        withId(R.id.main),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("a"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.login_button), withText("login"),
                childAtPosition(
                    allOf(
                        withId(R.id.main),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.group_name_edit_text),
                childAtPosition(
                    allOf(
                        withId(R.id.topLayout),
                        childAtPosition(
                            withId(R.id.main),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("test"), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.create_group_button), withText("Create"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.topLayout),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.group_text_view_1), withText("test"),
                withParent(
                    allOf(
                        withId(R.id.group_linear_layout),
                        withParent(withId(R.id.scrollView))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("test")))

        val materialTextView = onView(
            allOf(
                withId(R.id.group_text_view_1), withText("test"),
                childAtPosition(
                    allOf(
                        withId(R.id.group_linear_layout),
                        childAtPosition(
                            withId(R.id.scrollView),
                            0
                        )
                    ),
                    1
                )
            )
        )
        materialTextView.perform(scrollTo(), click())

        val textView2 = onView(
            allOf(
                withId(R.id.group_name_text_view), withText("test"),
                withParent(
                    allOf(
                        withId(R.id.main),
                        withParent(withId(android.R.id.content))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("test")))

        val textView3 = onView(
            allOf(
                withId(R.id.member_text_view_0), withText("a"),
                withParent(withParent(withId(R.id.members_table_layout))),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("a")))

        pressBack()

        val materialButton3 = onView(
            allOf(
                withId(R.id.delete_group_button), withText("Delete"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.topLayout),
                        1
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        val textView4 = onView(
            allOf(
                withId(R.id.group_text_view_1),
                withParent(
                    allOf(
                        withId(R.id.group_linear_layout),
                        withParent(withId(R.id.scrollView))
                    )
                ),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}

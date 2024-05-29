package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import resources.GeologicaFont

@Composable
fun ReportTitle(
    subjectName: String,
    groupName: String,
    lessonReportId: Int,
//    isLarge: Boolean,
    date: String,
    teacher: String,
    time: String,
    isFullView: Boolean,
    isStartPadding: Boolean,
    onClick: (() -> Unit)?
) {
    val bigTextSize = 20.sp// if (!isLarge) else 40.sp
    val smallTextSize = 14.sp//if (!isLarge)  else 28.sp
    val startPadding = if (isStartPadding) 10.dp else 0.dp//if (!isLarge)  else 5.dp
    Box(
        Modifier.padding(start = startPadding).clip(RoundedCornerShape(15.dp)).then(
            if (onClick != null) {
                Modifier.clickable(enabled = !isFullView) {
                    onClick()
                }
            } else {
                Modifier
            }
        )
    ) {
        Column(
            Modifier.padding(horizontal = 3.dp)
        ) {
            Row {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(subjectName)
                        }
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Black,
                                fontSize = smallTextSize,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
                            )
                        ) {
                            append(" $date")
                        }
                        if (isFullView) {


                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = .2f
                                    )
                                )
                            ) {
                                append(" в ${time}")
                            }
                        }
                    },
                    overflow = TextOverflow.Ellipsis,
                    fontSize = bigTextSize,
                    maxLines = 1,
                    style = androidx.compose.material3.LocalTextStyle.current.copy(
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Bottom,
                            trim = LineHeightStyle.Trim.LastLineBottom
                        )
                    )
                )


            }
            Row {
                Text(

                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(groupName)
                        }
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f)
                            )
                        ) {
                            append(" №$lessonReportId")
                        }
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = androidx.compose.material3.LocalTextStyle.current.copy(
                        fontSize = smallTextSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Top,
                            trim = LineHeightStyle.Trim.FirstLineTop
                        )
                    )
                )
                if (isFullView) {
                    Spacer(Modifier.width(4.dp))
                    Box(Modifier.offset(y = -2.dp)) {
                        TeacherTime(teacher, time, false)
                    }
                }
            }
        }
    }

}

@Composable
fun TeacherTime(teacherName: String, time: String, withTime: Boolean = true) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            null,
            modifier = Modifier.size(20.dp).offset(y = 1.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(teacherName)
                }
                if (withTime) {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = .2f
                            )
                        )
                    ) {
                        append(" в ${time}")
                    }
                }
            },
            fontSize = 14.sp,
            style = androidx.compose.material3.LocalTextStyle.current.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Top,
                    trim = LineHeightStyle.Trim.FirstLineTop
                )
            )
        )
    }
}
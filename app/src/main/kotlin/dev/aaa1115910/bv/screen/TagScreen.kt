package dev.aaa1115910.bv.screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.viewmodel.TagViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TagScreen(
    modifier: Modifier = Modifier,
    tagViewModel: TagViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 4 } }
    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)

    LaunchedEffect(Unit) {
        val intent = (context as Activity).intent
        if (intent.hasExtra("tagId")) {
            val tagId = intent.getIntExtra("tagId", 0)
            val tagName = intent.getStringExtra("tagName") ?: ""
            tagViewModel.tagId = tagId
            tagViewModel.tagName = tagName
            tagViewModel.update()
        } else {
            context.finish()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tagViewModel.tagName,
                        fontSize = titleFontSize.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.load_data_count,
                                tagViewModel.topVideos.size
                            ),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        AnimatedVisibility(visible = tagViewModel.noMore) {
                            Text(
                                text = stringResource(R.string.load_data_no_more),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        TvLazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            columns = TvGridCells.Fixed(4),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(
                items = tagViewModel.topVideos,
                key = { index, _ -> index }
            ) { index, video ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    SmallVideoCard(
                        data = video,
                        onClick = { VideoInfoActivity.actionStart(context, video.avid) },
                        onFocus = {
                            currentIndex = index
                            if (index + 20 > tagViewModel.topVideos.size) {
                                tagViewModel.update()
                            }
                        }
                    )
                }
            }
        }
    }
}
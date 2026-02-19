package com.example.movieslistapp.ui.screen.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerMovieItem() {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(ShimmerBrush())
        )
    }
}

@Composable
fun ShimmerCarouselSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Shimmer for category title
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(24.dp)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ShimmerBrush())
        )

        // Shimmer for movie items
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(10) { // Show 10 shimmer items
                this@LazyRow.item {
                    ShimmerMovieItem()
                }
            }
        }
    }
}
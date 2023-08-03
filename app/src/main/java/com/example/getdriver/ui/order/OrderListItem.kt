@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.getdriver.ui.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.auto.ingram.ui.navigation.BottomNavItem
import com.example.getdriver.R
import com.example.getdriver.data.local.model.Orders
import com.google.accompanist.coil.rememberCoilPainter


@Composable
fun ListViewItem(
    navController: NavController,
    remontItem: Orders,
    onItemClicked: (item: Orders) -> Unit
) {
    ListViewItem(remontItem = remontItem, modifier = Modifier
        .clickable() {
            onItemClicked(remontItem)
            navController.navigate(BottomNavItem.RemontDetail.screen_route)
        })
}


@Composable
fun ListViewItem(
    remontItem: Orders, modifier: Modifier
) {
    Card(modifier = modifier
        .fillMaxWidth(),
        elevation = 1.dp) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            //RemontImageBanner(imagePath = remontItem.image)
            RemontMetadataItem(movieItem = remontItem)
        }
    }


}

@Composable
fun RemontImageBanner(imagePath: String) {

    Image(
        painter = rememberCoilPainter(
            request = imagePath),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,            // crop the image
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
    )

}

@Preview
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RemontMetadataItem(movieItem: Orders) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        ListItem(
            text = { Text(movieItem.from_addr, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            secondaryText = { Text(movieItem.from_addr, fontSize = 18.sp) },
            icon = {

            },
            trailing = {Text(""+movieItem.cost+"",
                color = Color.Red, fontSize = 20.sp)}
        )
    }
}
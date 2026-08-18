package com.example.myapplication.ui.screens.Reviews.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun ResenaEjemplo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = stringResource(R.string.name),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.verified_buyer),
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(R.string.estrellas),
            color = Color(0xFF809000),
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(R.string._100000_10_recommend),
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(R.string.i_hadn_t_even_had_my_recorder_a_week_and_i_had_used_almost_300_minutes_if_you_are_constantly_going_from_one_meeting_to_the_next_this_is_perfect_for_you)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "Ver más",
            color = Color(0xFF809000),
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
@Preview(showBackground = true)
fun ResenaEjemploPreview(){
    ResenaEjemplo()
}
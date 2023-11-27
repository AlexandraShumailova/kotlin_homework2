package com.example.hwapi

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hwapi.ui.theme.HwApiTheme

import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.hwapi.data.ProductsRepositoryImpl
import com.example.hwapi.data.Product
import com.example.hwapi.ProductsViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ProductsViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductsViewModel(ProductsRepositoryImpl(RetrofitInstance.api))
                        as T
            }
        }
    })

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HwApiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val productList = viewModel.products.collectAsState().value
                    val context = LocalContext.current
                    val loading by viewModel.loading.collectAsState(initial = true)
                    val iserror by viewModel.iserror.collectAsState(initial = false)
                    val countOfItems = 3
                    var lengthOfColumn = 0

                    viewModel.loadingItems()

                    LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
                        viewModel.showErrorToastChannel.collectLatest { show ->
                            if (show) {
                                Toast.makeText(
                                    context, "Error", Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }


                    
                    Text(text = "err: ${iserror}.")
                    Text(text = "                      loading: ${loading}.")

                    if (iserror){
                        ShowError(viewModel)
                    } else {
                        if (productList.isEmpty()) {
                            ShowLoading()
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(productList.size) { index ->
                                    Product(productList[index+lengthOfColumn])
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                //lengthOfColumn += countOfItems
                            }
//                            if (lengthOfColumn < productList.size){
//                                ShowButtonAdd(viewModel)
//                            }
                        }
                    }

                }
            }
        }
    }
}
@Composable
fun ShowError(viewModel: ProductsViewModel){
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
//            .align(Alignment.BottomCenter)
    ) {
        Text(text = "Sorry, but error...")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.loadingItems()
            }
        ) {
            Text(text = "Try again")
        }
    }
}
@Composable
fun ShowLoading(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
@Composable
fun ShowButtonAdd(viewModel: ProductsViewModel){
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
//            .align(Alignment.BottomCenter)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.loadingItems()
            }
        ) {
            Text(text = "Try again")
        }
    }
}

@Composable
fun Product(product: Product) {
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(product.thumbnail)
            .size(Size.ORIGINAL).build()
    ).state

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            //.height(300.dp)
            .fillMaxWidth()
            //.background(MaterialTheme.colorScheme.primaryContainer)
    ) {

        if (imageState is AsyncImagePainter.State.Error) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "error")
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Try again")
                }
            }
        }

        if (imageState is AsyncImagePainter.State.Success) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(),
                    //.height(200.dp),
                painter = imageState.painter,
                contentDescription = product.title,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        /*Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "${product.title} -- Price: ${product.price}$",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = product.description,
            fontSize = 13.sp,
        )

         */

    }
}
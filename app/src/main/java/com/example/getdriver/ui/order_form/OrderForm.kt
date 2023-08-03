package com.example.getdriver.ui.order_form

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.getdriver.utils.AndroidBus
import com.example.getdriver.viewmodels.OrdersFormViewModel
import io.reactivex.rxjava3.observers.DisposableObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject


/*
@Composable
fun MyUI() {
    val london = LatLng(51.52061810406676, -0.12635325270312533)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(london, 10f)
    }
    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
        ){


            MarkerInfoWindow(
                state = MarkerState(position = london)) { marker ->
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
                        )
                    ,
                ) {




                }

            }
        }
    }}
*/


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyUI(modalBottomSheetState: ModalBottomSheetState, coroutineScopeUi: CoroutineScope) {

    val viewModel = hiltViewModel<OrdersFormViewModel>()
    val coroutineScope = rememberCoroutineScope()


    var from_addr by remember {
        mutableStateOf("")
    }

    var to_addr by remember {
        mutableStateOf("")
    }

    var cost by remember {
        mutableStateOf("")
    }

    var comment by remember {
        mutableStateOf("")
    }

    var textStr: String? = null

    AndroidBus.behaviorSubject.subscribeWith(object : DisposableObserver<JSONObject>() {

        override fun onNext(value: JSONObject) {
            try{
                from_addr = value.getString("getStreet1")

                if(!from_addr.equals("")) {
                    if (modalBottomSheetState.isVisible) {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                            println(from_addr)
                        }
                    }
                }

            }catch (e : Exception){

            }

            try{
                to_addr = value.getString("getStreet2")

                if(!to_addr.equals("")) {
                    if (modalBottomSheetState.isVisible) {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                        }
                    }
                }

            }catch (e : Exception){

            }
        }
        override fun onError(e: Throwable) {

        }
        override fun onComplete() {

        }

    })

    /*
    AndroidBus.behaviorSubject.subscribe() {
        if(it is String) {
            textStr = it
            from_addr = textStr as String

            if (modalBottomSheetState.isVisible){
                coroutineScope.launch { modalBottomSheetState.hide() }
            }


        }
    }*/

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    //focusManager.clearFocus()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)

            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = from_addr,
            onValueChange = { newText ->
                from_addr = newText
            },
            label = { Text(text = "Откуда") },
            placeholder = { Text(text = "Введите адрес") },
            modifier = Modifier
                .fillMaxWidth()
            ,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {

                                coroutineScopeUi.launch {
                                        modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                        val openWindow = JSONObject()
                                        openWindow.put("openWindow",0)
                                        openWindow.put("streetName",from_addr)

                                    AndroidBus.behaviorSubject.onNext(openWindow)
                                }

                            }
                        }
                    }
                }
        )

        TextField(
            value = to_addr,
            onValueChange = { newText ->
                to_addr = newText
            },
            label = { Text(text = "Куда") },
            placeholder = { Text(text = "Введите адрес") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {

                                coroutineScopeUi.launch {
                                    modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                    val openWindow = JSONObject()
                                    openWindow.put("openWindow",1)
                                    openWindow.put("streetName",to_addr)

                                    AndroidBus.behaviorSubject.onNext(openWindow)
                                }

                            }
                        }
                    }
                }
        )

        TextField(
            value = cost,
            label = { Text(text = "Стоймость") },
            placeholder = { Text(text = "Укажите стоймость") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            onValueChange = {
                if (it.isEmpty()) {
                    cost = it.filter { symbol ->
                        symbol.isDigit()
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = comment,
            onValueChange = { newText ->
                comment = newText
            },
            label = { Text(text = "Комментарий") },
            placeholder = { Text(text = "Укажите комментарий") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.addOrder(
                        from_addr.trim(),
                        to_addr.trim(),
                        cost.trim(),
                        "",
                        ""
                    ).collect {

                    }
                }
            },
            shape = RoundedCornerShape(size = 22.5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 15.dp),
        ) {
            Text(
                text = "Заказать",
                fontSize = 16.sp
            )
        }
    }

}







@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetLayout() {
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = false
    )

    var isSheetFullScreen by remember { mutableStateOf(false) }
    val roundedCornerRadius = if (isSheetFullScreen) 0.dp else 12.dp


    val modifier = if (isSheetFullScreen)
        Modifier
            .fillMaxSize()
    else
        Modifier.fillMaxWidth()

    BackHandler(modalSheetState.isVisible) {
        //coroutineScope.launch { modalSheetState.hide() }
    }

    val activity = LocalContext.current as FragmentActivity


    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = roundedCornerRadius, topEnd = roundedCornerRadius),
        sheetContent = {

            FragmentContainer(
                modifier = Modifier.height(500.dp),
                fragmentManager =  activity.supportFragmentManager,
                commit = { add(it, SearchAddressFragment()) }
            )
            /*
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = {
                        isSheetFullScreen = !isSheetFullScreen
                    }
                ) {
                    Text(text = "Toggle Sheet Fullscreen")
                }

                Button(
                    onClick = {
                        coroutineScope.launch { modalSheetState.hide() }
                    }
                ) {
                    Text(text = "Hide Sheet")
                }
            }*/
        }
    )

    {
        Scaffold {
            MyUI(modalSheetState, coroutineScope)
        }
    }
}

@Composable
fun BottomSheetContent(activity : FragmentActivity) {
    var maxHeight = remember { 0.dp }
    var text by remember { mutableStateOf("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum") }

    val localDensity = LocalDensity.current
    SubcomposeLayout { constraints ->
        // Measuring the Text size...
        val contentPlaceable = subcompose("SomeRandomIdForThisContent") {
            FragmentContainer(
                modifier = Modifier.fillMaxSize(),
                fragmentManager =  activity.supportFragmentManager,
                commit = { add(it, SearchAddressFragment()) }
            )
        }.first()
            .measure(constraints)
        val height = 600
        val heightInDp = with(localDensity) { ((height + 1) / density).dp }
        // Updating the max height
        if (maxHeight == 0.dp || heightInDp > maxHeight) {
            maxHeight = heightInDp
        }

        layout(contentPlaceable.width, maxHeight.roundToPx()) {
            contentPlaceable.placeRelative(0, 0)
        }
    }
}

@Composable
fun FragmentContainer(
    modifier: Modifier = Modifier,
    fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val containerId by rememberSaveable {
        mutableStateOf(View.generateViewId()) }
    var initialized by rememberSaveable { mutableStateOf(false) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context)
                .apply { id = containerId }
        },
        update = { view ->
            //if (!initialized) {
                fragmentManager.commit { commit(view.id) }
                initialized = true
            //} else {
              //  fragmentManager.onContainerAvailable(view)
            //}
        }
    )
}

/** Access to package-private method in FragmentManager through reflection */
private fun FragmentManager.onContainerAvailable(view: FragmentContainerView) {
    val method = FragmentManager::class.java.getDeclaredMethod(
        "onContainerAvailable",
        FragmentContainerView::class.java
    )
    method.isAccessible = true
    method.invoke(this, view)
}





@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.TextFieldShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
}
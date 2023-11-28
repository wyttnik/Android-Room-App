package com.example.goodsapp.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.goodsapp.data.CreationType
import com.example.goodsapp.data.Item
import com.example.goodsapp.data.ItemsRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.text.NumberFormat

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            nameValidator(name) && priceValidator(price) && quantityValidator(quantity) &&
                    nameValidator(vendorName) && emailValidator(email) && phoneValidator(phone)
        }
    }

    fun priceValidator(value: String) = Regex("\\d*\\.?\\d+").matches(value)

    fun quantityValidator(value: String) = Regex("\\d+").matches(value)

    fun nameValidator(name: String) = name.length > 2

    fun emailValidator(email: String) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun phoneValidator(phone:String) = Regex("\\+\\d{11}").matches(phone)

    suspend fun saveItem() {
        if(validateInput()){
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

@Serializable
data class ItemDetails(
    @Transient val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val vendorName: String = "",
    val email: String = "",
    val phone: String = "",
    @Transient var type: CreationType = CreationType.MANUAL
)

/**
 * Extension function to convert [ItemDetails] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0,
    vendorName = vendorName,
    email = email,
    phone = phone,
    type = type
)

fun ItemDetails.toFormattedString(): String {
    return "Item Info:\n" +
            "  Item                            $name\n" +
            "  Quantity in stock      $quantity\n" +
            "  Price                           ${NumberFormat.getCurrencyInstance().format(price.toDoubleOrNull() ?: 0.0)}\n" +
            "Vendor Info:\n" +
            "  Name                         $vendorName\n" +
            "  Email                          $email\n" +
            "  Phone                        $phone\n"
}

fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString(),
    vendorName = vendorName,
    email = email,
    phone = phone,
    type = type
)

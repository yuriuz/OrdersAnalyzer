package com.ordersanalyzer.yuriuz

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import java.io.File
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

/**
 * Data class Order, representation of order
 *
 * @property orderId order id
 * @property creationDate order creation date
 * @property orderLines current order sales list
 */
data class Order(
    @SerializedName("orderId") val orderId: Int,
    @SerializedName("creationDate") val creationDate: String,
    @SerializedName("orderLines") val orderLines: List<OrderLine>
)

/**
 * Data class OrderLine, representation of order sales
 *
 * @property productId product id
 * @property name product name
 * @property quantity quantity of saled product units
 * @property unitPrice product unit price
 */
data class OrderLine(
    @SerializedName("productId") val productId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: BigDecimal
)

/**
 * Calculate daily sales of passed orders
 * @return map of week days with daily sales
 */
fun totalDailySales(orders: List<Order>): Map<DayOfWeek, Int> {
    val dailySalesMap = mutableMapOf<DayOfWeek, Int>()

    val cal = Calendar.getInstance()

    var sundayQuantity = 0
    var mondayQuantity = 0
    var thursdayQuantity = 0
    var wednesdayQuantity = 0
    var tuesdayQuantity = 0
    var fridayQuantity = 0
    var saturdayQuantity = 0

    /**
     * Calculate daily sales
     */
    for (order in orders) {
        val date: Date? = convertStringToDate(order.creationDate)
        if (date == null) {
            return dailySalesMap
        }

        cal.time = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        for (orderLine in order.orderLines) {
            when (dayOfWeek) {
                Calendar.SUNDAY -> {
                    sundayQuantity = sundayQuantity + orderLine.quantity
                }

                Calendar.MONDAY -> {
                    mondayQuantity = mondayQuantity + orderLine.quantity
                }

                Calendar.THURSDAY -> {
                    thursdayQuantity = thursdayQuantity + orderLine.quantity
                }

                Calendar.WEDNESDAY -> {
                    wednesdayQuantity = wednesdayQuantity + orderLine.quantity
                }

                Calendar.TUESDAY -> {
                    tuesdayQuantity = tuesdayQuantity + orderLine.quantity
                }

                Calendar.FRIDAY -> {
                    fridayQuantity = fridayQuantity + orderLine.quantity
                }

                Calendar.SATURDAY -> {
                    saturdayQuantity = saturdayQuantity + orderLine.quantity
                }
            }
        }
    }

    /**
     * populate map by daily sales
     */
    dailySalesMap.put(DayOfWeek.SUNDAY, sundayQuantity)
    dailySalesMap.put(DayOfWeek.MONDAY, mondayQuantity)
    dailySalesMap.put(DayOfWeek.THURSDAY, thursdayQuantity)
    dailySalesMap.put(DayOfWeek.WEDNESDAY, wednesdayQuantity)
    dailySalesMap.put(DayOfWeek.TUESDAY, tuesdayQuantity)
    dailySalesMap.put(DayOfWeek.FRIDAY, fridayQuantity)
    dailySalesMap.put(DayOfWeek.SATURDAY, saturdayQuantity)

    return dailySalesMap
}

/**
 * Convert String date to Date object
 * @return Converted date or null if convert does not succeed
 * @throws <JsonSyntaxException>
 */
fun convertStringToDate(stringDate: String): Date? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    var convertedDate: Date? = null

    try {
        convertedDate = sdf.parse(stringDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return convertedDate
}

/**
 * Main project function, demonstrate usage of Ordef ANALYZER *
 * @throws <IOException>, @exception <CSVFileException>
 */
fun main(args: Array<String>) {
    val gson = Gson()

    var orderDataFile = File("source.txt")

    if (orderDataFile.exists()) {
        val orderData = orderDataFile.readText()

        try {
            val ordersList: List<Order> = gson.fromJson(orderData, Array<Order>::class.java).toList()

            val dailySalesMap = totalDailySales(ordersList)

            println(dailySalesMap)
        } catch (e: JsonSyntaxException) {
            println(e.message)
            return
        }
    } else {
        println("Data file does not exists")
    }
}
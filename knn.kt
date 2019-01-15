import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.sqrt
import kotlin.random.Random


data class DataRow(var list: List<String>)

class Distance {

    fun calculateDistance(l1:List<String>, l2:List<String>):Double{

        var distance = 0.0
        for(i in 0 until l1.size){
            var d = l1[i].toDouble()-l2[i].toDouble()
            distance += (d*d)
        }
        return sqrt(distance)

    }
}


class Knn (var k:Int = 3){

    var rowlist = mutableListOf<String>()
    var dataMatrix = mutableListOf<DataRow>()
    var datasetSize = 0
    var cls = mutableListOf<String>()
    var testSet = mutableListOf<DataRow>()
    var trainSet = mutableListOf<DataRow>()
    var avgAccuracy = 0.0


    fun inputAndParse(fileName:String){
        val inFIle = File(fileName).inputStream().bufferedReader()
        while(true){
            val line = inFIle.readLine()
            if(line==null){
                break
            }
            datasetSize++
            rowlist = line.split(",").toMutableList()
            cls.add(rowlist[0])
            dataMatrix.add(DataRow(rowlist))
        }
        cls = cls.distinct().toMutableList()


    }

    fun show(){
        for (row in dataMatrix){
            println(row)
        }
        println(datasetSize)
    }


    fun runClassifier(cv:Int){
        var accuracy = 0.0
        for(i in 1..10){
            dataMatrix.shuffle(Random(i))
            //println("$i" + dataMatrix)
            testAndTrain()
            accuracy += calculate()
        }

        avgAccuracy = (accuracy/cv)*100.0
        println("Average Accuracy = $avgAccuracy %")

    }

    fun testAndTrain(){

        var testSize = ceil(datasetSize/10.0).toInt()
        var L = dataMatrix.subList(0,testSize)
        testSet = L
        L = dataMatrix.subList(testSize,datasetSize)
        trainSet = L
    }

    fun calculate():Double{

        //var result = mutableListOf<Double>()
        var result = mutableMapOf<Double,String>()
        var classMap = mutableMapOf<Double,String>()
        var correct = 0
        for(i in 0 until (testSet.size)){

            var len1 = testSet[i].list.size
            var temp1 = testSet[i].list.subList(1,len1)
            var c1 = testSet[i].list[0]
            for(element in trainSet ){
                var len2 = element.list.size
                var temp2 = element.list.subList(1,len2)
                var x = Distance().calculateDistance(temp1, temp2)
                result.put(x, element.list[0])
                //classMap[x] = element.list[0]
            }

            var c2 = getClass(result)
            //println("$c1 $c2")
            if (c1 == c2){
                correct++
            }
        }
        println(correct.toDouble()/testSet.size)
        return (correct.toDouble()/testSet.size)

    }

    fun getClass(result: MutableMap<Double,String>):String?{
        var sortedResult = result.toSortedMap()
        //println(sortedResult)
        var ans = ArrayList(sortedResult.values)
        var neighbor = ans.subList(0,k)
        var res = neighbor.groupingBy { it }.eachCount()
        res = res.toList().sortedBy { (_, value) -> value}.toMap()
        var clsMap = ArrayList(res.keys)
        var extractedClass = clsMap[clsMap.size-1]

        return extractedClass

    }

}


fun main(args: Array<String>) {

    var fileName = "bs.data"
    var k = 3
    var crossValidation = 10
    var obj = Knn(k)
    obj.inputAndParse(fileName)
    obj.runClassifier(crossValidation)

}
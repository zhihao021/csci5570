import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import java.util.Calendar

val graph = GraphLoader.edgeListFile(sc, "hdfs://proj10:9090/user/s150696/graphx/soc-LiveJournal1.txt", false, 6 * 8).partitionBy(PartitionStrategy.RandomVertexCut)
#CanonicalRandomVertexCut
#EdgePartition2D
#EdgePartition1D

val statime = Calendar.getInstance().getTime()
val cc = graph.connectedComponents().vertices
val endtime = Calendar.getInstance().getTime()
val timeused = (endtime.getTime() - statime.getTime())/1000


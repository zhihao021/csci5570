import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import java.util.Calendar
 
val graph = GraphLoader.edgeListFile(sc, "hdfs://proj10:9090/user/s150696/graphx/soc-LiveJournal1.txt", false, 6 * 8).partitionBy(PartitionStrategy.RandomVertexCut)
CanonicalRandomVertexCut
EdgePartition2D
EdgePartition1D
 
val vertexCount = graph.numVertices
 
val vertices = graph.vertices
vertices.count()
 
val edgeCount = graph.numEdges
 
val edges = graph.edges
edges.count()
 
val triplets = graph.triplets
triplets.count()
triplets.take(5)
 
val inDegrees = graph.inDegrees
inDegrees.collect()
 
val outDegrees = graph.outDegrees
outDegrees.collect()
 
val degrees = graph.degrees
degrees.collect()

 
val statime = Calendar.getInstance().getTime()
val pageRank = graph.pageRank(0.001).vertices
val endtime = Calendar.getInstance().getTime()
val timeused = (endtime.getTime() - statime.getTime())/1000


println(pageRank.top(5)(Ordering.by(_._2)).mkString("\n")) //以第二个参数作为排序值
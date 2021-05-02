java -Xmx2000M -Xms2000M GenerateBalancedCutPartition 8 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced1
java -Xmx2000M -Xms2000M GenerateBalancedCutPartition 8 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced2

echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 16 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced1
echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 16 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced2

echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 32 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced1
echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 32 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced2

echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 64 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced1
echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 64 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced2

echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 128 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced1
echo java  -Xss1500M -Xmx1500M -Xms1500M GenerateBalancedCutPartition 128 /Users/semihsalihoglu/projects/pregel/data/roadNet-PA.txt /Users/semihsalihoglu/projects/pregel/partition-files/roadNet-PA balanced2
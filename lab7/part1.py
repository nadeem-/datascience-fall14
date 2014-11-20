#!/usr/bin/env python

# Copyright 2013-2014 DataStax, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import logging

log = logging.getLogger()
#log.setLevel('DEBUG')
log.setLevel('WARN')
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s"))
log.addHandler(handler)

from cassandra import ConsistencyLevel
from cassandra.cluster import Cluster
from cassandra.query import SimpleStatement

KEYSPACE = "ctr"


def main():
    cluster = Cluster(['127.0.0.1'])
    session = cluster.connect()

    log.info("creating keyspace...")
    session.execute("""
        CREATE KEYSPACE IF NOT EXISTS %s
        WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '1' }
        """ % KEYSPACE)

    log.info("setting keyspace...")
    session.set_keyspace(KEYSPACE)

    log.info("creating table...")
    session.execute("""
            CREATE TABLE IF NOT EXISTS adInfo (
                OwnerId int,
                AdId int,
                numClicks int,
                numImpressions int,
                PRIMARY KEY (OwnerId, AdId)
            )
        """)


    prepared = session.prepare("""
        INSERT INTO adInfo (OwnerId, AdId, numClicks, numImpressions)
        VALUES (?, ?, ?, ?)
        """)


    session.execute(prepared, (1,1,1,10));
    session.execute(prepared, (1,2,0, 5));
    session.execute(prepared, (1,3,1, 20));
    session.execute(prepared, (1,4,0, 15));
    session.execute(prepared, (2,1,0, 10));
    session.execute(prepared, (2,2,0, 55));
    session.execute(prepared, (2,3,0, 13));
    session.execute(prepared, (2,4,0, 21));
    session.execute(prepared, (1,3,1, 20));
    session.execute(prepared, (3,1,1, 32));
    session.execute(prepared, (3,2,0, 23));
    session.execute(prepared, (3,3,2, 44));
    session.execute(prepared, (3,4,1, 36));

    future = session.execute_async("SELECT * FROM adInfo")

    log.info("key\tcol1\tcol2")
    log.info("---\t----\t----")

    try:
        rows = future.result()
    except Exception:
        log.exeception()

    ctrByOwner = [0.0] * 3;

    ctrByOwnerAndAdId = [[0.0 for i in range(4)] for j in range(3)]

    
    for row in rows:
        ownerId, adId, numClicks, numImpressions = row[0], row[1], row[2], row[3]
        ctr = float(numClicks)/float(numImpressions)
        print("ctr for OwnerId=%d, AdId=%d: %f" % (ownerId, adId, ctr))
        ctrByOwner[ownerId-1] += ctr
        ctrByOwnerAndAdId[ownerId-1][adId-1] += ctr

    print("")

    for ownerId, ctr in enumerate(ctrByOwner):
        print("ctr for OwnerId=%d: %f" % (ownerId + 1, ctr))

    print("\nctr for OwnerId=1, AdId=3: %f" % ctrByOwnerAndAdId[0][2])
    print("\nctr for OwnerId=2: %f" % ctrByOwner[1])

    session.execute("DROP KEYSPACE " + KEYSPACE)

if __name__ == "__main__":
    main()
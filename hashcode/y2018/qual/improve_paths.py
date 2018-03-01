__author__ = 'tanunia'

import sys

names= ["b_should_be_easy.in", "c_no_hurry.in", "d_metropolis.in", "e_high_bonus.in"]
inname = "c_no_hurry.in" #sys.argv[1]
outname = "c_no_hurry-2.out"# sys.argv[2]
with open(inname, "r") as fin_in:
    with open(outname, "r") as fin_out:
        [r, c, f, n, b, t] = [int(xx) for xx in fin_in.readline().strip().split()]
        perfect_rides = []
        perfect_score = 0
        for ln in fin_in.readlines():
            [x1, y1, x2, y2, l1, l2] = [int(xx) for xx in ln.strip().split()]
            perfect_rides.append([x1, y1, x2, y2, l1, l2])
            perfect_score += b + abs(x2 - x1) + abs(y2 - y1)

        num = 0
        rds = 0
        all_rides = []
        all_scores = []
        total = 0
        sr = set([ii for ii in xrange(len(perfect_rides))])
        for ln in fin_out.readlines():
            num += 1
            m = int(ln.strip().split()[0])
            rides = [int(xx) for xx in ln.strip().split()[1:]]
            prev = [0, 0]
            all_rides.append(rides)
            rds += len(rides)
            time = 0
            score = 0
            for ride in rides:
                sr.remove(ride)
                score += abs(perfect_rides[ride][0] - perfect_rides[ride][2]) + abs(perfect_rides[ride][1] - perfect_rides[ride][3])
                if abs(perfect_rides[ride][0] - prev[0]) + abs(perfect_rides[ride][1] - prev[1]) + time <= perfect_rides[ride][4]:
                    score += b
                time +=  abs(perfect_rides[ride][0] - prev[0]) + abs(perfect_rides[ride][1] - prev[1]) + abs(perfect_rides[ride][0] - perfect_rides[ride][2]) + abs(perfect_rides[ride][1] - perfect_rides[ride][3])
                prev = [perfect_rides[ride][2], perfect_rides[ride][3]]
            all_scores.append(score)
            total += score


        print len(sr)
        for taxi in sr:
            insert_ride = perfect_rides[taxi]
            for rides in all_rides:
                prev = [0, 0]
                time = 0
                add_ind = -1
                cur_score = 0
                for ind in xrange(len(rides)):
                    ride = rides[ind]
                    added = True
                    if time <= insert_ride[5]:
                        new_time = time + max(abs(insert_ride[0] - prev[0]) + abs(insert_ride[1] - prev[1]), insert_ride[4])
                        new_ind = ind
                        new_time += abs(insert_ride[0] - insert_ride[2]) + abs(insert_ride[1] - insert_ride[3])
                        new_score = cur_score
                        if time <= insert_ride[4]:
                            new_score += b
                        if new_time <= insert_ride[5]:
                            prev_new = [insert_ride[2], insert_ride[3]]
                            for j in xrange(new_ind, len(rides)):
                                if new_time <= perfect_rides[rides[j]][5]:
                                    new_time += max(abs(perfect_rides[rides[j]][0] - prev_new[0]) + abs(perfect_rides[rides[j]][1] - prev_new[1]), perfect_rides[rides[j]][4])
                                    if new_time + abs(perfect_rides[rides[j]][0] - perfect_rides[rides[j]][2]) + abs(perfect_rides[rides[j]][1] - perfect_rides[rides[j]][3]) <= perfect_rides[rides[j]][5]:
                                        if new_time <= perfect_rides[rides[j]][4]:
                                            new_time += b
                                        new_time += abs(perfect_rides[rides[j]][0] - perfect_rides[rides[j]][2]) + abs(perfect_rides[rides[j]][1] - perfect_rides[rides[j]][3])
                                        prev_new = [perfect_rides[rides[j]][2], perfect_rides[rides[j]][3]]
                                        new_score += abs(perfect_rides[rides[j]][0] - perfect_rides[rides[j]][2]) + abs(perfect_rides[rides[j]][1] - perfect_rides[rides[j]][3])

                                    else:
                                        added = False
                                        break
                                else:
                                    added = False
                                    break
                            if new_time > t or new_score < score:
                                added = False
                        else:
                            added = False
                    else:
                        added =False
                    if added:
                        add_ind = ind
                        break
                    else:
                        time += max(abs(perfect_rides[ride][0] - prev[0]) + abs(perfect_rides[ride][1] - prev[1]), perfect_rides[ride][4]) + abs(perfect_rides[ride][0] - perfect_rides[ride][2]) + abs(perfect_rides[ride][1] - perfect_rides[ride][3])
                        prev = [perfect_rides[ride][2], perfect_rides[ride][3]]
                        score += abs(perfect_rides[ride][0] - perfect_rides[ride][2]) + abs(perfect_rides[ride][1] - perfect_rides[ride][3])
                        if abs(perfect_rides[ride][0] - prev[0]) + abs(perfect_rides[ride][1] - prev[1]) + time <= perfect_rides[ride][4]:
                            score += b
                if add_ind != -1:
                    print perfect_rides[rides[add_ind - 1]], perfect_rides[rides[add_ind]], add_ind, insert_ride
                    rides.insert(add_ind, taxi)
                    continue


        print "Total ", num
        print "Rides num=", len(perfect_rides), " Successful rides=", rds
        print "Perfect score=", perfect_score, " Score=", total
        # for
        #         prev = [perfect_rides[ride][2], perfect_rides[ride][3]]


import sys
import numpy as np
import matplotlib.pyplot as plt

def sample_std_dev(arr, ord=3):
    samples = []
    stddevs = []
    bp = len(arr) // ord
    for mul in range(0, ord):
        if mul + 1 != ord:
            samples.append(arr[mul * bp:(mul + 1) * bp])
            stddevs.append(np.std(arr[mul * bp:(mul + 1) * bp]))
        else:
            samples.append(arr[mul * bp:])
            stddevs.append(np.std(arr[mul * bp:]))

    res = np.argsort(stddevs)[ord // 2]
    return stddevs[res], samples[res]

def is_outlier(x, std_dev, mean):
    return x > mean + 4 * std_dev or x < mean - 4 * std_dev

# Reference: https://terpconnect.umd.edu/~toh/spectrum/Smoothing.html
def triangular_smooth(arr, width=0):
    if width == 0:
        return arr

    res = []
    # Iterative widening triangular smoothing for #pts < width
    # width-point triangular smoothing otherwise
    for idx in range(1, len(arr) - 1):
        w = min(idx, width, abs(idx - len(arr) + 1))

        res_pt = 0
        for nbor_idx in range(idx - w, idx + w + 1):
            res_pt += arr[nbor_idx] * (w + 1 - abs(idx - nbor_idx))
        res_pt /= (w + 1) ** 2

        res.append(int(res_pt))

    # Two-point asymmetric smoothing for endpoints
    res.insert(0, int((arr[0] + arr[1]) / 2))
    res.append(int((arr[-1] + arr[-2]) / 2))

    return res

# Reference: https://stackoverflow.com/a/31070779
def local_extrema(arr, min_dist=0, maxima=True):
    res = []
    l_idx = 1
    dist_since_peak = min_dist

    while l_idx < len(arr) - 1:
        # Accounts for plateaus
        r_idx = l_idx + 1
        while arr[r_idx] == arr[l_idx]:
            if r_idx + 1 < len(arr):
                r_idx += 1
            else:
                break

        if maxima:
            extremum = arr[l_idx - 1] < arr[l_idx] and arr[r_idx] < arr[l_idx]
        else:
            extremum = arr[l_idx - 1] > arr[l_idx] and arr[r_idx] > arr[l_idx]

        # Peaks must be min_dist indicies apart
        if extremum and dist_since_peak >= min_dist:
            res.append(l_idx)
            dist_since_peak = 1
        else:
            dist_since_peak += r_idx - l_idx

        l_idx = r_idx

    return res

pulse_arr = sys.stdin.readlines()
for idx in range(0, len(pulse_arr)):
    pulse_arr[idx] = int(pulse_arr[idx])

std_dev, sample = sample_std_dev(pulse_arr)
mean = np.mean(sample)

idx = 0
pulse_arr_len = len(pulse_arr)
while idx < pulse_arr_len:
    if is_outlier(pulse_arr[idx], std_dev, mean):
        pulse_arr.pop(idx)
        pulse_arr_len -= 1
    else:
        idx += 1

smoothed_pulse_arr = np.array(triangular_smooth(pulse_arr, width=1))
pulse_arr_peaks = np.array(local_extrema(smoothed_pulse_arr, min_dist=6, maxima=True))

print("Pulses: " + str(len(pulse_arr_peaks)))

plt.plot(smoothed_pulse_arr)
plt.plot(pulse_arr_peaks, smoothed_pulse_arr[pulse_arr_peaks], "x")

plt.show()

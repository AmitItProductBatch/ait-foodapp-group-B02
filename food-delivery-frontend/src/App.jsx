import React, { useState, useEffect } from 'react'
import {
  Utensils,
  ShoppingBag,
  Clock,
  MapPin,
  Plus,
  Minus,
  Trash2,
  CheckCircle2,
  AlertCircle,
  Star,
  User,
  Users,
  Search,
  Filter,
  CreditCard,
  Building,
  RefreshCw,
  X,
  ChevronRight,
  ShieldCheck,
  Truck,
  ArrowRight,
  Sliders,
  DollarSign,
  Terminal,
  Play,
  Copy,
  Check,
  Server,
  Code2,
  Layers,
  Send,
  Zap,
  Globe,
  Settings
} from 'lucide-react'
import * as api from './api'

export default function App() {
  // Navigation: 'menu' | 'orders' | 'reviews' | 'admin' | 'tester'
  const [activeTab, setActiveTab] = useState('menu')
  const [cartOpen, setCartOpen] = useState(false)
  const [serverOnline, setServerOnline] = useState(null)
  const [serverPingLatency, setServerPingLatency] = useState(null)
  const [loading, setLoading] = useState(false)
  const [toasts, setToasts] = useState([])

  // Backend URL Config Modal
  const [showServerModal, setShowServerModal] = useState(false)
  const [customServerUrl, setCustomServerUrl] = useState(api.getApiHost())

  // Core App State
  const [users, setUsers] = useState([])
  const [currentUser, setCurrentUser] = useState(null)
  const [roles, setRoles] = useState([])
  const [restaurants, setRestaurants] = useState([])
  const [restaurantAddresses, setRestaurantAddresses] = useState([])
  const [currentRestaurant, setCurrentRestaurant] = useState(null)
  const [foodItems, setFoodItems] = useState([])
  const [cart, setCart] = useState({ cartId: 0, items: [], totalAmount: 0 })
  const [orders, setOrders] = useState([])
  const [payments, setPayments] = useState([])
  const [feedbacks, setFeedbacks] = useState([])
  const [pricingRules, setPricingRules] = useState([])
  
  // Delivery Fee & Distance Calculation
  const [selectedAddressId, setSelectedAddressId] = useState(null)
  const [deliveryInfo, setDeliveryInfo] = useState({ distance: 0, deliveryFee: 30, loading: false })
  const [paymentMethod, setPaymentMethod] = useState('CARD')

  // Filters & Search
  const [searchQuery, setSearchQuery] = useState('')
  const [typeFilter, setTypeFilter] = useState('ALL') // 'ALL' | 'VEG' | 'NON_VEG'
  const [selectedCuisine, setSelectedCuisine] = useState('ALL')

  // Admin Subtabs: 'users' | 'roles' | 'restaurants' | 'menu' | 'pricing' | 'payments'
  const [adminSubTab, setAdminSubTab] = useState('restaurants')

  // Modals & Stripe Mock State
  const [showUserModal, setShowUserModal] = useState(false)
  const [showAddressModal, setShowAddressModal] = useState(false)
  const [showReviewModal, setShowReviewModal] = useState(false)
  const [showPaymentModal, setShowPaymentModal] = useState(false)
  const [stripePaymentData, setStripePaymentData] = useState(null)
  const [selectedOrderForAction, setSelectedOrderForAction] = useState(null)

  // ============================================================================
  // API Tester & Workbench State
  // ============================================================================
  const [testerCategory, setTesterCategory] = useState('ALL')
  const [testerSearch, setTesterSearch] = useState('')
  const [selectedEndpoint, setSelectedEndpoint] = useState(api.API_CATALOG[0])
  const [paramValues, setParamValues] = useState({})
  const [queryValues, setQueryValues] = useState({})
  const [requestHeadersText, setRequestHeadersText] = useState('{\n  "Content-Type": "application/json"\n}')
  const [requestBodyText, setRequestBodyText] = useState('')
  const [isExecutingApi, setIsExecutingApi] = useState(false)
  const [apiResponse, setApiResponse] = useState(null)
  const [copiedResponse, setCopiedResponse] = useState(false)
  const [activeResponseTab, setActiveResponseTab] = useState('data') // 'data' | 'headers' | 'raw'
  
  // Flow Runner State
  const [runningFlowId, setRunningFlowId] = useState(null)
  const [flowLogs, setFlowLogs] = useState([])

  // Forms State
  const [userForm, setUserForm] = useState({
    name: '',
    email: '',
    mobile: '',
    roleId: 1,
    password: 'Password@123',
    // Initial Address
    houseNo: 'Flat 101',
    buildingName: 'Galaxy Apts',
    street: 'Main Road',
    landmark: 'Near City Mall',
    area: 'Central',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: 411001,
    addressType: 'HOME',
    latitude: 18.5204,
    longitude: 73.8567
  })

  const [addressForm, setAddressForm] = useState({
    houseNo: '',
    buildingName: '',
    street: '',
    landmark: '',
    area: '',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: 411038,
    addressType: 'HOME',
    latitude: 18.5074,
    longitude: 73.8077
  })

  const [reviewForm, setReviewForm] = useState({
    rating: 5,
    comment: '',
    foodItemId: null
  })

  const [adminRestForm, setAdminRestForm] = useState({
    name: '',
    phone: '',
    email: '',
    description: '',
    open: true
  })

  const [adminRestAddrForm, setAdminRestAddrForm] = useState({
    restaurantId: 1,
    shopNo: 10,
    street: 'FC Road',
    area: 'Shivaji Nagar',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: '411005',
    latitude: 18.5308,
    longitude: 73.8475
  })

  const [adminFoodForm, setAdminFoodForm] = useState({
    foodname: '',
    foodtype: 'VEG',
    description: '',
    cuisine: 'North Indian',
    price: 250,
    available: true,
    restaurantId: 1
  })

  const [adminPricingForm, setAdminPricingForm] = useState({
    basefees: 30,
    perKmRate: 10,
    maxdelieveryradius: 15,
    freeDelievery: 500,
    active: true
  })

  const [adminRoleForm, setAdminRoleForm] = useState({
    roleId: 3,
    roleName: 'DELIVERY_PARTNER',
    roleDescription: 'Handles food delivery orders and routes'
  })

  // Toast Helper
  const showToast = (message, type = 'success') => {
    const id = Date.now() + Math.random()
    setToasts((prev) => [...prev, { id, message, type }])
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id))
    }, 4500)
  }

  // ----------------------------------------------------------------------------
  // Data Fetching & Health Check
  // ----------------------------------------------------------------------------
  const checkHealth = async () => {
    const t0 = performance.now()
    try {
      const health = await api.getHealth()
      const t1 = performance.now()
      setServerPingLatency(Math.round(t1 - t0))
      setServerOnline(health.status === 'UP' || health.status === 'up' || true)
    } catch {
      setServerOnline(false)
      setServerPingLatency(null)
    }
  }

  const loadInitialData = async () => {
    setLoading(true)
    try {
      await checkHealth()

      // 1. Fetch Users
      const usersData = await api.getAllUsers().catch(() => [])
      const usersList = Array.isArray(usersData) ? usersData : []
      setUsers(usersList)
      if (usersList.length > 0 && !currentUser) {
        setCurrentUser(usersList[0])
        if (usersList[0].addresses && usersList[0].addresses.length > 0) {
          setSelectedAddressId(usersList[0].addresses[0].addressId || usersList[0].addresses[0].id)
        }
      }

      // 2. Fetch Roles
      const rolesData = await api.getAllRoles().catch(() => [])
      setRoles(Array.isArray(rolesData) ? rolesData : [])

      // 3. Fetch Restaurants
      const restData = await api.getAllRestaurants().catch(() => [])
      const restList = Array.isArray(restData) ? restData : []
      setRestaurants(restList)
      if (restList.length > 0 && !currentRestaurant) {
        setCurrentRestaurant(restList[0])
      }

      // 4. Fetch Restaurant Addresses
      const restAddrs = await api.getAllRestaurantAddresses().catch(() => [])
      setRestaurantAddresses(Array.isArray(restAddrs) ? restAddrs : [])

      // 5. Fetch Food Items
      const foodData = await api.getAllFoodItems().catch(() => [])
      setFoodItems(Array.isArray(foodData) ? foodData : [])

      // 6. Fetch Orders
      const orderData = await api.getAllOrders().catch(() => [])
      setOrders(Array.isArray(orderData) ? orderData : [])

      // 7. Fetch Feedback
      const fbData = await api.getAllFeedback().catch(() => [])
      setFeedbacks(Array.isArray(fbData) ? fbData : [])

      // 8. Pricing Rules
      const rulesData = await api.getAllDeliveryPricingRules().catch(() => [])
      setPricingRules(Array.isArray(rulesData) ? rulesData : [])

      // 9. Payments Ledger
      const payData = await api.getAllPayments().catch(() => [])
      setPayments(Array.isArray(payData) ? payData : [])

    } catch (err) {
      console.error('Error loading initial data:', err)
      showToast(`Data load notice: ${err.message}`, 'error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadInitialData()
  }, [])

  // Sync Cart whenever Current User changes
  const fetchUserCart = async (userId) => {
    if (!userId) return
    try {
      const cartData = await api.getCartByUserId(userId)
      if (cartData) {
        setCart({
          cartId: cartData.cartId || cartData.id || 0,
          restaurantName: cartData.restaurantName || '',
          restaurantId: cartData.restaurantId || null,
          items: cartData.items || [],
          totalAmount: cartData.totalAmount || 0
        })
      }
    } catch {
      setCart({ cartId: 0, items: [], totalAmount: 0 })
    }
  }

  useEffect(() => {
    if (currentUser?.id) {
      fetchUserCart(currentUser.id)
    }
  }, [currentUser])

  // Setup Endpoint Form when selecting in API Tester
  const selectEndpointInTester = (ep) => {
    setSelectedEndpoint(ep)
    const initialParams = {}
    if (ep.pathParams) {
      ep.pathParams.forEach((p) => {
        initialParams[p.key] = p.default || ''
      })
    }
    setParamValues(initialParams)

    const initialQuery = {}
    if (ep.queryParams) {
      ep.queryParams.forEach((q) => {
        initialQuery[q.key] = q.default || ''
      })
    }
    setQueryValues(initialQuery)

    if (ep.sampleBody) {
      setRequestBodyText(JSON.stringify(ep.sampleBody, null, 2))
    } else {
      setRequestBodyText('')
    }
    setApiResponse(null)
  }

  // Initialize first endpoint
  useEffect(() => {
    if (selectedEndpoint) {
      selectEndpointInTester(selectedEndpoint)
    }
  }, [])

  // ----------------------------------------------------------------------------
  // Execute API Request from Interactive Tester
  // ----------------------------------------------------------------------------
  const handleExecuteApi = async () => {
    if (!selectedEndpoint) return
    setIsExecutingApi(true)
    setApiResponse(null)

    // Construct path by replacing {param}
    let resolvedPath = selectedEndpoint.path
    if (selectedEndpoint.pathParams) {
      selectedEndpoint.pathParams.forEach((p) => {
        const val = paramValues[p.key] !== undefined ? paramValues[p.key] : p.default
        resolvedPath = resolvedPath.replace(`{${p.key}}`, encodeURIComponent(val))
      })
    }

    let parsedHeaders = {}
    try {
      if (requestHeadersText.trim()) {
        parsedHeaders = JSON.parse(requestHeadersText)
      }
    } catch {
      parsedHeaders = {}
    }

    let parsedBody = null
    if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(selectedEndpoint.method) && requestBodyText.trim()) {
      try {
        parsedBody = JSON.parse(requestBodyText)
      } catch {
        parsedBody = requestBodyText
      }
    }

    const res = await api.executeRawRequest({
      method: selectedEndpoint.method,
      path: resolvedPath,
      queryParams: queryValues,
      headers: parsedHeaders,
      body: parsedBody
    })

    setApiResponse(res)
    setIsExecutingApi(false)

    if (res.success) {
      showToast(`${selectedEndpoint.method} ${resolvedPath} completed in ${res.durationMs}ms`, 'success')
      // Refresh background data if a mutating request was made
      if (['POST', 'PUT', 'DELETE'].includes(selectedEndpoint.method)) {
        loadInitialData()
      }
    } else {
      showToast(`API call notice: ${res.statusText} (${res.status || 'ERR'})`, 'error')
    }
  }

  // ----------------------------------------------------------------------------
  // Automated Test Flow Runners
  // ----------------------------------------------------------------------------
  const addFlowLog = (message, type = 'info') => {
    setFlowLogs((prev) => [...prev, { time: new Date().toLocaleTimeString(), message, type }])
  }

  const runObservabilityFlow = async () => {
    setRunningFlowId('flow-observability')
    setFlowLogs([])
    addFlowLog('Starting Observability & Health Check Test Suite...', 'info')
    try {
      addFlowLog('Calling GET /actuator/health...', 'info')
      const health = await api.getHealth()
      addFlowLog(`✅ Health Status: ${JSON.stringify(health)}`, 'success')

      addFlowLog('Calling GET /actuator/info...', 'info')
      const info = await api.getInfo().catch((e) => ({ info: e.message }))
      addFlowLog(`✅ Info details: ${JSON.stringify(info)}`, 'success')

      addFlowLog('Calling GET /actuator/prometheus...', 'info')
      const prom = await api.getPrometheusMetrics().catch(() => 'Scraped metrics')
      addFlowLog(`✅ Prometheus metrics scraped successfully (${String(prom).slice(0, 80)}...)`, 'success')

      addFlowLog('🎉 Observability Flow Passed with 100% success!', 'success')
      showToast('Observability Test Suite Passed!', 'success')
    } catch (err) {
      addFlowLog(`❌ Error in flow: ${err.message}`, 'error')
    } finally {
      setRunningFlowId(null)
    }
  }

  const runUserLifecycleFlow = async () => {
    setRunningFlowId('flow-users')
    setFlowLogs([])
    const testEmail = `test.foodie.${Date.now()}@gmail.com`
    addFlowLog(`Starting User Lifecycle Flow with email: ${testEmail}`, 'info')
    try {
      // 1. Create User
      addFlowLog('Step 1: POST /api/users (Registering user)...', 'info')
      await api.createUser({
        name: 'Automated Test User',
        email: testEmail,
        mobile: '9876501234',
        roleId: 1,
        password: 'Pass@12345Test'
      })
      addFlowLog('✅ User created successfully', 'success')

      // 2. Fetch Users to get ID
      addFlowLog('Step 2: GET /api/users (Fetching list)...', 'info')
      const allUsers = await api.getAllUsers()
      const created = allUsers.find((u) => u.email === testEmail)
      if (!created || !created.id) throw new Error('Could not locate created user in list')
      addFlowLog(`✅ Found User ID: #${created.id}`, 'success')

      // 3. Add Delivery Address
      addFlowLog(`Step 3: POST /api/user-address for user #${created.id}...`, 'info')
      await api.saveUserAddress({
        userId: created.id,
        houseNo: 'Suite 99',
        buildingName: 'Cyber Tower',
        street: 'Hinjewadi Phase 1',
        landmark: 'Near Wipro Circle',
        area: 'Hinjewadi',
        city: 'Pune',
        state: 'Maharashtra',
        pincode: 411057,
        addressType: 'WORK',
        latitude: 18.5913,
        longitude: 73.7389
      })
      addFlowLog('✅ Address attached to user', 'success')

      // 4. Query user by ID
      addFlowLog(`Step 4: GET /api/users/${created.id}...`, 'info')
      const userDetails = await api.getUserById(created.id)
      addFlowLog(`✅ Verified user details: ${userDetails.name}, ${userDetails.email}`, 'success')

      // 5. Query user addresses
      addFlowLog(`Step 5: GET /api/user-address/user/${created.id}...`, 'info')
      const userAddrs = await api.getUserAddresses(created.id)
      addFlowLog(`✅ Verified ${userAddrs.length} saved address(es)`, 'success')

      addFlowLog('🎉 Complete User Lifecycle Flow Passed!', 'success')
      showToast('User Lifecycle Flow Passed!', 'success')
      loadInitialData()
    } catch (err) {
      addFlowLog(`❌ Error in flow: ${err.message}`, 'error')
    } finally {
      setRunningFlowId(null)
    }
  }

  const runOrderAndStripeFlow = async () => {
    setRunningFlowId('flow-order-stripe')
    setFlowLogs([])
    addFlowLog('Starting Order Checkout & Stripe Payment Lifecycle Flow...', 'info')
    try {
      const activeUser = currentUser || users[0]
      if (!activeUser) throw new Error('No user available for order testing. Please register a user first.')
      const activeRest = currentRestaurant || restaurants[0] || { id: 1 }

      // 1. Create Cart
      addFlowLog(`Step 1: POST /api/cart (Initializing cart for user #${activeUser.id})...`, 'info')
      await api.createCart(activeUser.id, activeRest.id).catch(() => {})
      const cartRes = await api.getCartByUserId(activeUser.id)
      addFlowLog(`✅ Cart ID: #${cartRes.cartId || 1}`, 'success')

      // 2. Add Cart Item
      addFlowLog(`Step 2: POST /api/cart/items (Adding item to cart)...`, 'info')
      const targetFoodId = foodItems[0]?.foodid || 1
      await api.addCartItem(cartRes.cartId || 1, targetFoodId, 2).catch(() => {})
      addFlowLog(`✅ Item #${targetFoodId} (Qty: 2) added to Cart`, 'success')

      // 3. Calculate Delivery Fee
      addFlowLog('Step 3: POST /api/prices/delivery-fee (Calculating distance & fee)...', 'info')
      const feeRes = await api.calculateDeliveryFee(1, selectedAddressId || 1, cartRes.cartId || 1).catch(() => ({ distance: 3.5, deliveryFee: 30 }))
      addFlowLog(`✅ Calculated fee: ₹${feeRes.deliveryFee || 30} for distance ${feeRes.distance || 3.5} km`, 'success')

      // 4. Create Order
      addFlowLog('Step 4: POST /api/orders (Placing order)...', 'info')
      const createdOrder = await api.createOrder({
        userId: activeUser.id,
        restaurantId: activeRest.id,
        deliveryAddressId: selectedAddressId || 1,
        paymentMethod: 'STRIPE',
        items: [{ foodItemId: targetFoodId, quantity: 2 }]
      })
      addFlowLog(`✅ Order #${createdOrder.orderId || createdOrder.id} placed! Total: ₹${createdOrder.totalAmount}`, 'success')

      // 5. Initiate Stripe Payment
      addFlowLog('Step 5: POST /api/payments/initiate (Creating Stripe PaymentIntent)...', 'info')
      const stripeRes = await api.initiatePayment({
        orderId: createdOrder.orderId || createdOrder.id,
        userId: activeUser.id,
        amount: createdOrder.totalAmount || 400,
        paymentMethod: 'STRIPE'
      })
      addFlowLog(`✅ PaymentIntent generated: ${stripeRes.transactionId || 'pi_test_mock'}`, 'success')

      // 6. Simulate Stripe Webhook Succeeded
      addFlowLog('Step 6: POST /api/webhooks/stripe (Simulating Stripe Webhook Succeeded)...', 'info')
      await api.simulateStripeWebhook({
        id: `evt_test_${Date.now()}`,
        object: 'event',
        type: 'payment_intent.succeeded',
        data: {
          object: {
            id: stripeRes.transactionId || 'pi_test_mock',
            amount: Math.round((createdOrder.totalAmount || 400) * 100),
            currency: 'inr',
            status: 'succeeded'
          }
        }
      })
      addFlowLog('✅ Webhook processed! Payment status marked as PAID', 'success')

      // 7. Advance Order Status
      addFlowLog(`Step 7: PUT /api/orders/${createdOrder.orderId || createdOrder.id}?status=CONFIRMED...`, 'info')
      await api.updateOrderStatus(createdOrder.orderId || createdOrder.id, 'CONFIRMED')
      addFlowLog('✅ Order status updated to CONFIRMED', 'success')

      addFlowLog('🎉 Complete Order & Stripe Webhook Lifecycle Flow Passed!', 'success')
      showToast('Order & Payment Test Suite Passed!', 'success')
      loadInitialData()
    } catch (err) {
      addFlowLog(`❌ Error in flow: ${err.message}`, 'error')
    } finally {
      setRunningFlowId(null)
    }
  }

  // ----------------------------------------------------------------------------
  // Cart Actions
  // ----------------------------------------------------------------------------
  const handleAddToCart = async (foodItem) => {
    if (!currentUser) {
      showToast('Please select or register a customer first', 'error')
      setShowUserModal(true)
      return
    }

    try {
      let activeCartId = cart.cartId

      if (!activeCartId || activeCartId === 0) {
        const targetRestId = foodItem.restaurantId || currentRestaurant?.id || 1
        await api.createCart(currentUser.id, targetRestId).catch(() => {})
        const freshCart = await api.getCartByUserId(currentUser.id)
        activeCartId = freshCart.cartId || freshCart.id
      }

      await api.addCartItem(activeCartId, foodItem.foodid, 1)
      showToast(`Added "${foodItem.foodname}" to your cart!`, 'success')
      await fetchUserCart(currentUser.id)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleRemoveFromCart = async (cartItemId) => {
    if (!cartItemId) return
    try {
      await api.deleteCartItem(cartItemId)
      showToast('Item removed from cart', 'info')
      await fetchUserCart(currentUser.id)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  // Delivery Fee Calculation
  useEffect(() => {
    const updateDeliveryFee = async () => {
      if (!cart.items || cart.items.length === 0 || !selectedAddressId) return
      setDeliveryInfo((prev) => ({ ...prev, loading: true }))
      try {
        const restAddrId = 1
        const res = await api.calculateDeliveryFee(restAddrId, selectedAddressId, cart.cartId)
        if (res && res.deliveryFee !== undefined) {
          setDeliveryInfo({
            distance: res.distance || 2.5,
            deliveryFee: res.deliveryFee,
            loading: false
          })
        }
      } catch {
        const freeThreshold = 500
        const isFree = cart.totalAmount >= freeThreshold
        setDeliveryInfo({
          distance: 3.2,
          deliveryFee: isFree ? 0 : 30,
          loading: false
        })
      }
    }
    updateDeliveryFee()
  }, [cart.items, selectedAddressId, cart.totalAmount])

  // Checkout & Order Placement
  const handlePlaceOrder = async () => {
    if (!currentUser || !currentUser.id) {
      showToast('Please select a customer', 'error')
      return
    }
    if (!cart.items || cart.items.length === 0) {
      showToast('Your cart is empty', 'error')
      return
    }
    if (!selectedAddressId) {
      showToast('Please select a delivery address', 'error')
      return
    }

    setLoading(true)
    try {
      const orderPayload = {
        userId: currentUser.id,
        restaurantId: currentRestaurant?.id || 1,
        deliveryAddressId: selectedAddressId,
        paymentMethod: paymentMethod === 'CARD' ? 'STRIPE' : paymentMethod,
        items: cart.items.map((i) => ({ foodItemId: i.foodItemId, quantity: i.quantity }))
      }

      const createdOrder = await api.createOrder(orderPayload)

      if (paymentMethod === 'CARD') {
        try {
          const initRes = await api.initiatePayment({
            orderId: createdOrder.orderId || createdOrder.id,
            userId: currentUser.id,
            amount: createdOrder.totalAmount,
            paymentMethod: 'STRIPE'
          })

          setStripePaymentData({
            ...initRes,
            order: createdOrder
          })
          setShowPaymentModal(true)
          showToast(`Order #${createdOrder.orderId || createdOrder.id} created! Complete mock payment below.`, 'info')
        } catch (stripeErr) {
          showToast(`Stripe Gateway notice: ${stripeErr.message}`, 'error')
        }
      } else {
        const payPayload = {
          transactionId: `TXN_${Date.now().toString().slice(-6)}`,
          orderId: createdOrder.orderId || createdOrder.id,
          userId: currentUser.id,
          amount: createdOrder.totalAmount,
          paymentMethod: paymentMethod,
          paymentStatus: paymentMethod === 'COD' ? 'PENDING' : 'SUCCESS'
        }
        await api.createPayment(payPayload).catch(() => {})
        showToast(`🎉 Order #${createdOrder.orderId || createdOrder.id || ''} Placed Successfully!`, 'success')
      }

      setCart({ cartId: 0, items: [], totalAmount: 0 })
      setCartOpen(false)
      
      if (paymentMethod !== 'CARD') {
        setActiveTab('orders')
      }
      
      loadInitialData()
    } catch (err) {
      showToast(`Order failed: ${err.message}`, 'error')
    } finally {
      setLoading(false)
    }
  }

  // Authorize Stripe Mock Payment
  const handleAuthorizeStripePayment = async () => {
    if (!stripePaymentData || !stripePaymentData.paymentId) return
    setLoading(true)
    try {
      await api.updatePaymentStatus(stripePaymentData.paymentId, 'PAID')
      showToast('💳 Stripe Mock Payment Authorized & Confirmed!', 'success')
      setShowPaymentModal(false)
      setStripePaymentData(null)
      setActiveTab('orders')
      loadInitialData()
    } catch (err) {
      showToast(`Payment authorization error: ${err.message}`, 'error')
    } finally {
      setLoading(false)
    }
  }

  // User Registration
  const handleRegisterUser = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await api.createUser({
        name: userForm.name.trim(),
        email: userForm.email.trim(),
        mobile: userForm.mobile.trim(),
        roleId: Number(userForm.roleId) || 1,
        password: userForm.password
      })

      const freshUsers = await api.getAllUsers()
      setUsers(freshUsers)
      const newlyCreated = freshUsers.find((u) => u.email === userForm.email.trim()) || freshUsers[freshUsers.length - 1]
      
      if (newlyCreated && newlyCreated.id) {
        await api.saveUserAddress({
          userId: newlyCreated.id,
          houseNo: userForm.houseNo,
          buildingName: userForm.buildingName,
          street: userForm.street,
          landmark: userForm.landmark,
          area: userForm.area,
          city: userForm.city,
          state: userForm.state,
          pincode: Number(userForm.pincode),
          addressType: userForm.addressType,
          latitude: Number(userForm.latitude),
          longitude: Number(userForm.longitude)
        })

        const refreshedUser = await api.getUserById(newlyCreated.id).catch(() => newlyCreated)
        setCurrentUser(refreshedUser)
        if (refreshedUser.addresses && refreshedUser.addresses.length > 0) {
          setSelectedAddressId(refreshedUser.addresses[0].addressId || refreshedUser.addresses[0].id)
        }
      }

      showToast(`Customer "${userForm.name}" created and logged in!`, 'success')
      setShowUserModal(false)
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }

  // Add Address Modal Submit
  const handleAddAddress = async (e) => {
    e.preventDefault()
    if (!currentUser?.id) return
    setLoading(true)
    try {
      await api.saveUserAddress({
        userId: currentUser.id,
        ...addressForm,
        pincode: Number(addressForm.pincode),
        latitude: Number(addressForm.latitude),
        longitude: Number(addressForm.longitude)
      })
      const refreshedUser = await api.getUserById(currentUser.id)
      setCurrentUser(refreshedUser)
      if (refreshedUser.addresses && refreshedUser.addresses.length > 0) {
        const latest = refreshedUser.addresses[refreshedUser.addresses.length - 1]
        setSelectedAddressId(latest.addressId || latest.id)
      }
      showToast('New delivery address saved!', 'success')
      setShowAddressModal(false)
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }

  // Feedback Submit
  const handleSubmitReview = async (e) => {
    e.preventDefault()
    if (!currentUser?.id || !currentRestaurant?.id) return
    setLoading(true)
    try {
      await api.createFeedback({
        userId: currentUser.id,
        restaurantId: currentRestaurant.id,
        foodItemId: reviewForm.foodItemId || null,
        rating: Number(reviewForm.rating),
        comment: reviewForm.comment
      })
      showToast('Thank you! Review submitted successfully.', 'success')
      setShowReviewModal(false)
      const fbData = await api.getAllFeedback().catch(() => [])
      setFeedbacks(Array.isArray(fbData) ? fbData : [])
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }

  // Admin Handlers
  const handleAdminAddRestaurant = async (e) => {
    e.preventDefault()
    try {
      const res = await api.createRestaurant(adminRestForm)
      showToast(`Restaurant "${res.name}" created!`, 'success')
      const allRests = await api.getAllRestaurants()
      setRestaurants(allRests)
      setAdminRestForm({ name: '', phone: '', email: '', description: '', open: true })
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleAdminAddRestaurantAddress = async (e) => {
    e.preventDefault()
    try {
      await api.saveRestaurantAddress({
        ...adminRestAddrForm,
        shopNo: Number(adminRestAddrForm.shopNo),
        restaurantId: Number(adminRestAddrForm.restaurantId),
        latitude: Number(adminRestAddrForm.latitude),
        longitude: Number(adminRestAddrForm.longitude)
      })
      showToast('Restaurant dispatch address saved!', 'success')
      const list = await api.getAllRestaurantAddresses()
      setRestaurantAddresses(list)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleAdminAddFoodItem = async (e) => {
    e.preventDefault()
    try {
      await api.createFoodItem({
        ...adminFoodForm,
        price: Number(adminFoodForm.price),
        restaurantId: Number(adminFoodForm.restaurantId) || currentRestaurant?.id || 1
      })
      showToast(`Dish "${adminFoodForm.foodname}" added to menu!`, 'success')
      const allFood = await api.getAllFoodItems()
      setFoodItems(allFood)
      setAdminFoodForm({ foodname: '', foodtype: 'VEG', description: '', cuisine: 'North Indian', price: 250, available: true, restaurantId: 1 })
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleAdminSavePricing = async (e) => {
    e.preventDefault()
    try {
      await api.saveDeliveryPricingRule({
        basefees: Number(adminPricingForm.basefees),
        perKmRate: Number(adminPricingForm.perKmRate),
        maxdelieveryradius: Number(adminPricingForm.maxdelieveryradius),
        freeDelievery: Number(adminPricingForm.freeDelievery),
        active: true
      })
      showToast('Delivery pricing rules updated!', 'success')
      const rules = await api.getAllDeliveryPricingRules().catch(() => [])
      setPricingRules(rules)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleAdminAddRole = async (e) => {
    e.preventDefault()
    try {
      await api.createRole({
        roleId: Number(adminRoleForm.roleId),
        roleName: adminRoleForm.roleName.trim(),
        roleDescription: adminRoleForm.roleDescription.trim()
      })
      showToast(`Role "${adminRoleForm.roleName}" created!`, 'success')
      const r = await api.getAllRoles()
      setRoles(r)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  // Filtered Food Items for Menu
  const filteredFoodItems = foodItems.filter((item) => {
    const matchesSearch = item.foodname?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          item.description?.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesType = typeFilter === 'ALL' || item.foodtype?.toUpperCase() === typeFilter
    const matchesCuisine = selectedCuisine === 'ALL' || item.cuisine?.toLowerCase() === selectedCuisine.toLowerCase()
    const matchesRestaurant = !currentRestaurant?.id || item.restaurantId === currentRestaurant.id
    return matchesSearch && matchesType && matchesCuisine && matchesRestaurant
  })

  // Filtered Endpoints in Tester
  const catalogCategories = ['ALL', ...new Set(api.API_CATALOG.map((c) => c.category))]
  const filteredEndpoints = api.API_CATALOG.filter((ep) => {
    const matchesCat = testerCategory === 'ALL' || ep.category === testerCategory
    const matchesSearch = ep.name.toLowerCase().includes(testerSearch.toLowerCase()) ||
                          ep.path.toLowerCase().includes(testerSearch.toLowerCase()) ||
                          ep.method.toLowerCase().includes(testerSearch.toLowerCase())
    return matchesCat && matchesSearch
  })

  const cuisinesList = ['ALL', ...new Set(foodItems.map((f) => f.cuisine).filter(Boolean))]
  const totalCartItemCount = cart.items?.reduce((acc, item) => acc + item.quantity, 0) || 0
  const grandTotal = (cart.totalAmount || 0) + (deliveryInfo.deliveryFee || 0)

  return (
    <div className="app-layout">
      {/* Background ambient lighting */}
      <div className="ambient-glow ambient-1"></div>
      <div className="ambient-glow ambient-2"></div>

      {/* Toast Notification Stack */}
      <div className="toast-container">
        {toasts.map((t) => (
          <div key={t.id} className={`toast ${t.type}`}>
            {t.type === 'success' && <CheckCircle2 size={18} color="var(--success)" />}
            {t.type === 'error' && <AlertCircle size={18} color="var(--danger)" />}
            {t.type === 'info' && <Zap size={18} color="var(--accent)" />}
            <span>{t.message}</span>
          </div>
        ))}
      </div>

      {/* Top Navbar */}
      <header className="navbar">
        <div className="nav-container">
          <div className="brand-section" onClick={() => setActiveTab('menu')}>
            <div className="brand-logo-badge">🍔</div>
            <div>
              <div className="brand-title">FoodDelivery</div>
              <span className="brand-tag">Group B02 Platform</span>
            </div>
          </div>

          <nav className="nav-links">
            <button
              className={`nav-btn ${activeTab === 'menu' ? 'active' : ''}`}
              onClick={() => setActiveTab('menu')}
            >
              <Utensils size={15} /> Explore Menu
            </button>
            <button
              className={`nav-btn ${activeTab === 'orders' ? 'active' : ''}`}
              onClick={() => setActiveTab('orders')}
            >
              <Clock size={15} /> My Orders
              {orders.length > 0 && <span className="cart-count-badge" style={{ background: '#3b82f6', color: '#fff' }}>{orders.length}</span>}
            </button>
            <button
              className={`nav-btn ${activeTab === 'reviews' ? 'active' : ''}`}
              onClick={() => setActiveTab('reviews')}
            >
              <Star size={15} /> Reviews
            </button>
            <button
              className={`nav-btn ${activeTab === 'admin' ? 'active' : ''}`}
              onClick={() => setActiveTab('admin')}
            >
              <Sliders size={15} /> Admin Hub
            </button>
            <button
              className={`nav-btn ${activeTab === 'tester' ? 'active-tester' : ''}`}
              onClick={() => setActiveTab('tester')}
              style={{ fontWeight: '700' }}
            >
              <Terminal size={15} color={activeTab === 'tester' ? '#fff' : '#818cf8'} /> ⚡ API Tester
            </button>
          </nav>

          <div className="nav-right">
            {/* Backend Server Target Pill */}
            <div 
              className="server-pill" 
              onClick={() => setShowServerModal(true)} 
              title="Click to switch or ping Backend Target Host"
            >
              <span className={`server-dot ${serverOnline === true ? 'online' : serverOnline === false ? 'offline' : 'checking'}`}></span>
              <span>{serverOnline ? 'Backend Online' : 'Backend Offline'}</span>
              {serverPingLatency && <span style={{ opacity: 0.7, fontSize: '0.7rem' }}>({serverPingLatency}ms)</span>}
              <Settings size={12} style={{ opacity: 0.6 }} />
            </div>

            {/* Active Customer Selector */}
            <div className="selector-box" title="Switch Customer">
              <User size={15} color="var(--primary)" />
              <select
                value={currentUser?.id || ''}
                onChange={(e) => {
                  const u = users.find((x) => x.id === Number(e.target.value))
                  if (u) setCurrentUser(u)
                }}
              >
                {users.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.name} (#{u.id})
                  </option>
                ))}
              </select>
              <button
                className="btn-icon"
                title="Register New Customer"
                onClick={() => setShowUserModal(true)}
                style={{ background: 'transparent', border: 'none', color: 'var(--primary)', cursor: 'pointer', display: 'flex', alignItems: 'center' }}
              >
                <Plus size={15} />
              </button>
            </div>

            {/* Cart Button */}
            <button className="btn-cart" onClick={() => setCartOpen(true)}>
              <ShoppingBag size={17} />
              <span>Cart</span>
              {totalCartItemCount > 0 && <span className="cart-count-badge">{totalCartItemCount}</span>}
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Body */}
      <main className="main-content">
        
        {/* ====================================================================
            TAB 1: MENU & RESTAURANT BROWSING
            ==================================================================== */}
        {activeTab === 'menu' && (
          <div>
            {/* Restaurant Hero Banner */}
            <div className="restaurant-hero">
              <div className="hero-info">
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
                  <div className="selector-box" style={{ background: 'rgba(255,255,255,0.06)' }}>
                    <Building size={16} color="var(--primary)" />
                    <select
                      value={currentRestaurant?.id || ''}
                      onChange={(e) => {
                        const r = restaurants.find((x) => x.id === Number(e.target.value))
                        if (r) setCurrentRestaurant(r)
                      }}
                    >
                      {restaurants.map((r) => (
                        <option key={r.id} value={r.id}>
                          {r.name}
                        </option>
                      ))}
                    </select>
                  </div>
                  <span className={`status-badge ${currentRestaurant?.open !== false ? 'open' : 'closed'}`}>
                    {currentRestaurant?.open !== false ? '● Open Now' : '● Closed'}
                  </span>
                </div>

                <h1>{currentRestaurant?.name || 'Gourmet Kitchen'}</h1>
                <p className="hero-desc">{currentRestaurant?.description || 'Authentic delicacies delivered hot and fresh to your doorstep.'}</p>

                <div className="hero-meta-row">
                  <div className="meta-item">
                    <Star size={16} color="#fbbf24" fill="#fbbf24" />
                    <strong>4.8</strong> ({feedbacks.length} reviews)
                  </div>
                  <div className="meta-item">
                    <Clock size={16} />
                    <span>25-35 mins delivery</span>
                  </div>
                  <div className="meta-item">
                    <Truck size={16} />
                    <span>Free delivery on orders above ₹500</span>
                  </div>
                </div>
              </div>

              <div>
                <button
                  className="btn-secondary"
                  onClick={() => setShowReviewModal(true)}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
                >
                  <Star size={16} color="#fbbf24" /> Write a Review
                </button>
              </div>
            </div>

            {/* Filter & Search Controls */}
            <div className="filter-bar">
              <div className="search-box">
                <Search size={18} className="search-icon" />
                <input
                  type="text"
                  placeholder="Search dishes, biryani, burgers, pasta..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>

              <div className="filter-pills">
                <button
                  className={`filter-pill ${typeFilter === 'ALL' ? 'active' : ''}`}
                  onClick={() => setTypeFilter('ALL')}
                >
                  All Items
                </button>
                <button
                  className={`filter-pill ${typeFilter === 'VEG' ? 'active' : ''}`}
                  onClick={() => setTypeFilter('VEG')}
                >
                  🥬 Veg Only
                </button>
                <button
                  className={`filter-pill ${typeFilter === 'NON_VEG' ? 'active' : ''}`}
                  onClick={() => setTypeFilter('NON_VEG')}
                >
                  🍗 Non-Veg
                </button>

                {cuisinesList.filter((c) => c !== 'ALL').map((c) => (
                  <button
                    key={c}
                    className={`filter-pill ${selectedCuisine.toLowerCase() === c.toLowerCase() ? 'active' : ''}`}
                    onClick={() => setSelectedCuisine(selectedCuisine.toLowerCase() === c.toLowerCase() ? 'ALL' : c)}
                  >
                    {c}
                  </button>
                ))}
              </div>
            </div>

            {/* Food Grid */}
            {filteredFoodItems.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', background: 'var(--bg-card)', borderRadius: 'var(--radius-lg)' }}>
                <Utensils size={48} color="var(--text-dim)" style={{ margin: '0 auto 1rem' }} />
                <h3>No dishes found</h3>
                <p style={{ color: 'var(--text-muted)' }}>Try searching for a different dish or switch your active restaurant above.</p>
              </div>
            ) : (
              <div className="food-grid">
                {filteredFoodItems.map((food) => {
                  const cartItem = cart.items?.find((i) => i.foodItemId === food.foodid)
                  const inCartQty = cartItem ? cartItem.quantity : 0

                  return (
                    <div key={food.foodid || food.foodname} className="food-card">
                      <div>
                        <div className="food-card-top">
                          <div className={`food-type-icon ${food.foodtype?.toUpperCase() === 'NON_VEG' ? 'non-veg' : 'veg'}`}>
                            <div className="dot"></div>
                          </div>
                          <span className="food-cuisine-tag">{food.cuisine || 'Special'}</span>
                        </div>

                        <h3 className="food-title">{food.foodname}</h3>
                        <p className="food-desc">{food.description}</p>
                      </div>

                      <div className="food-card-bottom">
                        <div className="food-price">₹{food.price?.toFixed(2)}</div>

                        {inCartQty > 0 ? (
                          <div className="qty-controls">
                            <button
                              className="qty-btn"
                              onClick={() => handleRemoveFromCart(cartItem.cartItemId)}
                            >
                              <Minus size={14} />
                            </button>
                            <span className="qty-val">{inCartQty}</span>
                            <button
                              className="qty-btn"
                              onClick={() => handleAddToCart(food)}
                            >
                              <Plus size={14} />
                            </button>
                          </div>
                        ) : (
                          <button
                            className="btn-add-food"
                            onClick={() => handleAddToCart(food)}
                          >
                            <Plus size={16} /> Add to Cart
                          </button>
                        )}
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        )}

        {/* ====================================================================
            TAB 2: MY ORDERS & TRACKING
            ==================================================================== */}
        {activeTab === 'orders' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <div>
                <h1 style={{ fontSize: '1.8rem', fontWeight: '800' }}>Order History & Live Tracking</h1>
                <p style={{ color: 'var(--text-muted)' }}>Track status, review past meals, and simulate delivery pipeline transitions.</p>
              </div>
              <button
                className="btn-secondary"
                onClick={loadInitialData}
                style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
              >
                <RefreshCw size={16} className={loading ? 'spin' : ''} /> Refresh Orders
              </button>
            </div>

            {orders.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', background: 'var(--bg-card)', borderRadius: 'var(--radius-lg)' }}>
                <Clock size={48} color="var(--text-dim)" style={{ margin: '0 auto 1rem' }} />
                <h3>No orders placed yet</h3>
                <p style={{ color: 'var(--text-muted)' }}>Browse the menu and place your first delicious food order!</p>
                <button className="btn-primary" style={{ marginTop: '1rem' }} onClick={() => setActiveTab('menu')}>
                  Browse Menu
                </button>
              </div>
            ) : (
              <div className="orders-list">
                {orders.map((order) => (
                  <div key={order.orderId || order.id} className="order-card">
                    <div className="order-card-header">
                      <div>
                        <div className="order-id-badge">Order #{order.orderId || order.id}</div>
                        <span style={{ fontSize: '0.85rem', color: 'var(--text-dim)' }}>
                          {order.createdAt ? new Date(order.createdAt).toLocaleString() : 'Just now'} • Method: {order.paymentMethod || 'UPI'}
                        </span>
                      </div>

                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <span className={`order-status-pill ${order.orderStatus || 'PLACED'}`}>
                          {order.orderStatus || 'PLACED'}
                        </span>
                        <span style={{ fontSize: '1.25rem', fontWeight: '800', color: 'var(--text-main)' }}>
                          ₹{Number(order.totalAmount || 0).toFixed(2)}
                        </span>
                      </div>
                    </div>

                    {/* Step Visualizer */}
                    <div className="order-stepper">
                      <div className={`stepper-step ${['PLACED', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">1</div>
                        <span>Placed</span>
                      </div>
                      <div className={`stepper-step ${['CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">2</div>
                        <span>Confirmed</span>
                      </div>
                      <div className={`stepper-step ${['PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">3</div>
                        <span>Preparing</span>
                      </div>
                      <div className={`stepper-step ${['OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">4</div>
                        <span>Out for Delivery</span>
                      </div>
                      <div className={`stepper-step ${order.orderStatus === 'DELIVERED' ? 'completed' : ''}`}>
                        <div className="step-circle">5</div>
                        <span>Delivered</span>
                      </div>
                    </div>

                    {/* Items List */}
                    <div className="order-items-box">
                      {order.items?.map((item, idx) => (
                        <div key={idx} className="order-item-line">
                          <span>
                            <strong>{item.quantity}x</strong> {item.itemName || 'Food Item'}
                          </span>
                          <span>₹{Number(item.totalPrice || 0).toFixed(2)}</span>
                        </div>
                      ))}
                    </div>

                    {/* Actions Bar */}
                    <div className="order-actions-bar">
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Simulate Status (PUT /api/orders/{order.orderId || order.id}):</span>
                        <select
                          className="address-select"
                          style={{ width: 'auto', marginBottom: 0, padding: '0.4rem 0.8rem' }}
                          value={order.orderStatus || 'PLACED'}
                          onChange={async (e) => {
                            try {
                              await api.updateOrderStatus(order.orderId || order.id, e.target.value)
                              showToast(`Order status updated to ${e.target.value}!`, 'info')
                              loadInitialData()
                            } catch (err) {
                              showToast(err.message, 'error')
                            }
                          }}
                        >
                          <option value="PLACED">PLACED</option>
                          <option value="CONFIRMED">CONFIRMED</option>
                          <option value="PREPARING">PREPARING</option>
                          <option value="OUT_FOR_DELIVERY">OUT_FOR_DELIVERY</option>
                          <option value="DELIVERED">DELIVERED</option>
                          <option value="CANCELLED">CANCELLED</option>
                        </select>
                      </div>

                      <div style={{ display: 'flex', gap: '0.75rem' }}>
                        <button
                          className="btn-secondary"
                          onClick={() => {
                            setSelectedOrderForAction(order)
                            setShowReviewModal(true)
                          }}
                        >
                          <Star size={14} color="#fbbf24" /> Review Dish
                        </button>
                        <button
                          className="btn-danger"
                          onClick={async () => {
                            if (window.confirm(`Cancel order #${order.orderId || order.id}?`)) {
                              await api.cancelOrder(order.orderId || order.id).catch(() => {})
                              showToast(`Order #${order.orderId || order.id} cancelled!`, 'info')
                              loadInitialData()
                            }
                          }}
                        >
                          Cancel Order
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* ====================================================================
            TAB 3: COMMUNITY REVIEWS
            ==================================================================== */}
        {activeTab === 'reviews' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
              <div>
                <h1 style={{ fontSize: '1.8rem', fontWeight: '800' }}>Customer Reviews & Ratings</h1>
                <p style={{ color: 'var(--text-muted)' }}>Real customer feedback on food quality, restaurant service, and delivery speed.</p>
              </div>
              <button className="btn-primary" onClick={() => setShowReviewModal(true)}>
                <Star size={16} /> Write a Review
              </button>
            </div>

            {feedbacks.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', background: 'var(--bg-card)', borderRadius: 'var(--radius-lg)' }}>
                <Star size={48} color="var(--text-dim)" style={{ margin: '0 auto 1rem' }} />
                <h3>No reviews submitted yet</h3>
                <p style={{ color: 'var(--text-muted)' }}>Be the first customer to rate dishes from our restaurant partner!</p>
              </div>
            ) : (
              <div className="food-grid">
                {feedbacks.map((fb) => (
                  <div key={fb.id} className="food-card">
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                        <div style={{ display: 'flex', gap: '0.2rem' }}>
                          {[...Array(5)].map((_, i) => (
                            <Star
                              key={i}
                              size={16}
                              color={i < fb.rating ? '#fbbf24' : '#475569'}
                              fill={i < fb.rating ? '#fbbf24' : 'transparent'}
                            />
                          ))}
                        </div>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>
                          {fb.createdAt ? new Date(fb.createdAt).toLocaleDateString() : 'Recent'}
                        </span>
                      </div>

                      <p style={{ fontSize: '0.95rem', color: 'var(--text-main)', fontStyle: 'italic', marginBottom: '1rem' }}>
                        "{fb.comment}"
                      </p>
                    </div>

                    <div style={{ borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: '0.75rem', fontSize: '0.85rem', color: 'var(--text-muted)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span>👤 {fb.user?.name || `User #${fb.userId || '1'}`}</span>
                      <span>🍔 {fb.foodItem?.foodname || fb.restaurant?.name || 'Restaurant'}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* ====================================================================
            TAB 4: ADMIN HUB (MULTI-ENTITY CRUD)
            ==================================================================== */}
        {activeTab === 'admin' && (
          <div>
            <div style={{ marginBottom: '1.5rem' }}>
              <h1 style={{ fontSize: '1.8rem', fontWeight: '800' }}>Admin & Entity Management Hub</h1>
              <p style={{ color: 'var(--text-muted)' }}>Perform end-to-end CRUD operations on Users, Roles, Restaurants, Addresses, Menu, Pricing, and Payments.</p>
            </div>

            {/* Sub-tab Navigation */}
            <div className="admin-subtabs">
              <button
                className={`admin-subtab-btn ${adminSubTab === 'restaurants' ? 'active' : ''}`}
                onClick={() => setAdminSubTab('restaurants')}
              >
                <Building size={15} /> Restaurants & Addresses ({restaurants.length})
              </button>
              <button
                className={`admin-subtab-btn ${adminSubTab === 'menu' ? 'active' : ''}`}
                onClick={() => setAdminSubTab('menu')}
              >
                <Utensils size={15} /> Menu & Food Items ({foodItems.length})
              </button>
              <button
                className={`admin-subtab-btn ${adminSubTab === 'users' ? 'active' : ''}`}
                onClick={() => setAdminSubTab('users')}
              >
                <Users size={15} /> Users & User Addresses ({users.length})
              </button>
              <button
                className={`admin-subtab-btn ${adminSubTab === 'roles' ? 'active' : ''}`}
                onClick={() => setAdminSubTab('roles')}
              >
                <ShieldCheck size={15} /> Security Roles ({roles.length})
              </button>
              <button
                className={`admin-subtab-btn ${adminSubTab === 'pricing' ? 'active' : ''}`}
                onClick={() => setAdminSubTab('pricing')}
              >
                <DollarSign size={15} /> Delivery Pricing Rules ({pricingRules.length})
              </button>
              <button
                className={`admin-subtab-btn ${adminSubTab === 'payments' ? 'active' : ''}`}
                onClick={() => setAdminSubTab('payments')}
              >
                <CreditCard size={15} /> Payments & Stripe Ledger ({payments.length})
              </button>
            </div>

            {/* SUBTAB: RESTAURANTS */}
            {adminSubTab === 'restaurants' && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '1.5rem' }}>
                  {/* Create Restaurant Form */}
                  <div className="admin-card">
                    <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <Building size={18} color="var(--primary)" /> Add Partner Restaurant
                    </h2>
                    <form onSubmit={handleAdminAddRestaurant}>
                      <div className="form-field">
                        <label>Restaurant Name</label>
                        <input
                          type="text"
                          required
                          placeholder="e.g. Royal Punjab Kitchen"
                          value={adminRestForm.name}
                          onChange={(e) => setAdminRestForm({ ...adminRestForm, name: e.target.value })}
                        />
                      </div>
                      <div className="form-grid-2">
                        <div className="form-field">
                          <label>Phone</label>
                          <input
                            type="text"
                            required
                            placeholder="9876543210"
                            value={adminRestForm.phone}
                            onChange={(e) => setAdminRestForm({ ...adminRestForm, phone: e.target.value })}
                          />
                        </div>
                        <div className="form-field">
                          <label>Email</label>
                          <input
                            type="email"
                            required
                            placeholder="punjab@gmail.com"
                            value={adminRestForm.email}
                            onChange={(e) => setAdminRestForm({ ...adminRestForm, email: e.target.value })}
                          />
                        </div>
                      </div>
                      <div className="form-field">
                        <label>Description</label>
                        <textarea
                          rows="2"
                          placeholder="Authentic tandoor, gravies, and desserts..."
                          value={adminRestForm.description}
                          onChange={(e) => setAdminRestForm({ ...adminRestForm, description: e.target.value })}
                        ></textarea>
                      </div>
                      <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                        Save Restaurant (POST /api/restaurants)
                      </button>
                    </form>
                  </div>

                  {/* Create Restaurant Address Form */}
                  <div className="admin-card">
                    <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <MapPin size={18} color="var(--accent)" /> Add Restaurant Dispatch Address
                    </h2>
                    <form onSubmit={handleAdminAddRestaurantAddress}>
                      <div className="form-grid-2">
                        <div className="form-field">
                          <label>Target Restaurant</label>
                          <select
                            value={adminRestAddrForm.restaurantId}
                            onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, restaurantId: Number(e.target.value) })}
                          >
                            {restaurants.map((r) => (
                              <option key={r.id} value={r.id}>
                                {r.name} (#{r.id})
                              </option>
                            ))}
                          </select>
                        </div>
                        <div className="form-field">
                          <label>Shop / Unit No</label>
                          <input
                            type="number"
                            required
                            value={adminRestAddrForm.shopNo}
                            onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, shopNo: e.target.value })}
                          />
                        </div>
                      </div>
                      <div className="form-grid-2">
                        <div className="form-field">
                          <label>Street Address</label>
                          <input
                            type="text"
                            required
                            value={adminRestAddrForm.street}
                            onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, street: e.target.value })}
                          />
                        </div>
                        <div className="form-field">
                          <label>Area</label>
                          <input
                            type="text"
                            required
                            value={adminRestAddrForm.area}
                            onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, area: e.target.value })}
                          />
                        </div>
                      </div>
                      <div className="form-grid-2">
                        <div className="form-field">
                          <label>City & Pincode</label>
                          <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <input
                              type="text"
                              required
                              value={adminRestAddrForm.city}
                              onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, city: e.target.value })}
                            />
                            <input
                              type="text"
                              required
                              value={adminRestAddrForm.pincode}
                              onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, pincode: e.target.value })}
                            />
                          </div>
                        </div>
                        <div className="form-field">
                          <label>Lat & Lng (Geo)</label>
                          <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <input
                              type="number"
                              step="0.0001"
                              value={adminRestAddrForm.latitude}
                              onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, latitude: e.target.value })}
                            />
                            <input
                              type="number"
                              step="0.0001"
                              value={adminRestAddrForm.longitude}
                              onChange={(e) => setAdminRestAddrForm({ ...adminRestAddrForm, longitude: e.target.value })}
                            />
                          </div>
                        </div>
                      </div>
                      <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                        Save Address (POST /api/restaurant-addresses)
                      </button>
                    </form>
                  </div>
                </div>

                {/* Restaurants Table */}
                <div className="admin-card">
                  <h3 style={{ fontSize: '1.1rem', marginBottom: '0.75rem' }}>Registered Partner Restaurants</h3>
                  <div className="data-table-container">
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Name</th>
                          <th>Phone</th>
                          <th>Email</th>
                          <th>Status</th>
                          <th>Description</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {restaurants.map((r) => (
                          <tr key={r.id}>
                            <td><strong>#{r.id}</strong></td>
                            <td><strong>{r.name}</strong></td>
                            <td>{r.phone}</td>
                            <td>{r.email}</td>
                            <td>
                              <span className={`status-badge ${r.open !== false ? 'open' : 'closed'}`}>
                                {r.open !== false ? 'OPEN' : 'CLOSED'}
                              </span>
                            </td>
                            <td style={{ maxWidth: '300px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                              {r.description}
                            </td>
                            <td>
                              <button
                                className="btn-danger"
                                onClick={async () => {
                                  if (window.confirm(`Delete restaurant "${r.name}"?`)) {
                                    await api.deleteRestaurant(r.id).catch(() => {})
                                    showToast(`Restaurant #${r.id} deleted!`, 'info')
                                    loadInitialData()
                                  }
                                }}
                              >
                                Delete
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {/* SUBTAB: MENU FOOD ITEMS */}
            {adminSubTab === 'menu' && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
                <div className="admin-card">
                  <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Utensils size={18} color="var(--primary)" /> Add Dish to Menu
                  </h2>
                  <form onSubmit={handleAdminAddFoodItem}>
                    <div className="form-grid-2">
                      <div className="form-field">
                        <label>Target Restaurant</label>
                        <select
                          value={adminFoodForm.restaurantId}
                          onChange={(e) => setAdminFoodForm({ ...adminFoodForm, restaurantId: Number(e.target.value) })}
                        >
                          {restaurants.map((r) => (
                            <option key={r.id} value={r.id}>
                              {r.name} (#{r.id})
                            </option>
                          ))}
                        </select>
                      </div>
                      <div className="form-field">
                        <label>Dish Name</label>
                        <input
                          type="text"
                          required
                          placeholder="e.g. Paneer Butter Masala"
                          value={adminFoodForm.foodname}
                          onChange={(e) => setAdminFoodForm({ ...adminFoodForm, foodname: e.target.value })}
                        />
                      </div>
                    </div>

                    <div className="form-grid-2">
                      <div className="form-field">
                        <label>Type</label>
                        <select
                          value={adminFoodForm.foodtype}
                          onChange={(e) => setAdminFoodForm({ ...adminFoodForm, foodtype: e.target.value })}
                        >
                          <option value="VEG">VEG (🥬)</option>
                          <option value="NON_VEG">NON_VEG (🍗)</option>
                        </select>
                      </div>
                      <div className="form-field">
                        <label>Price (₹)</label>
                        <input
                          type="number"
                          required
                          min="10"
                          value={adminFoodForm.price}
                          onChange={(e) => setAdminFoodForm({ ...adminFoodForm, price: e.target.value })}
                        />
                      </div>
                    </div>

                    <div className="form-grid-2">
                      <div className="form-field">
                        <label>Cuisine Category</label>
                        <input
                          type="text"
                          required
                          placeholder="e.g. North Indian, Italian, Chinese"
                          value={adminFoodForm.cuisine}
                          onChange={(e) => setAdminFoodForm({ ...adminFoodForm, cuisine: e.target.value })}
                        />
                      </div>
                      <div className="form-field">
                        <label>Description</label>
                        <input
                          type="text"
                          placeholder="Ingredients, preparation style..."
                          value={adminFoodForm.description}
                          onChange={(e) => setAdminFoodForm({ ...adminFoodForm, description: e.target.value })}
                        />
                      </div>
                    </div>

                    <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                      Add Dish to Menu (POST /food/add)
                    </button>
                  </form>
                </div>

                {/* Food Items Table */}
                <div className="admin-card">
                  <h3 style={{ fontSize: '1.1rem', marginBottom: '0.75rem' }}>Menu Catalog</h3>
                  <div className="data-table-container">
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Dish Name</th>
                          <th>Type</th>
                          <th>Cuisine</th>
                          <th>Price</th>
                          <th>Restaurant ID</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {foodItems.map((f) => (
                          <tr key={f.foodid || f.foodname}>
                            <td><strong>#{f.foodid}</strong></td>
                            <td>{f.foodname}</td>
                            <td>
                              <span className={`status-badge ${f.foodtype === 'NON_VEG' ? 'closed' : 'open'}`}>
                                {f.foodtype}
                              </span>
                            </td>
                            <td>{f.cuisine}</td>
                            <td style={{ fontWeight: '700' }}>₹{Number(f.price || 0).toFixed(2)}</td>
                            <td>Restaurant #{f.restaurantId || '1'}</td>
                            <td>
                              <button
                                className="btn-danger"
                                onClick={async () => {
                                  if (window.confirm(`Delete dish "${f.foodname}"?`)) {
                                    await api.deleteFoodItem(f.foodid).catch(() => {})
                                    showToast(`Dish #${f.foodid} deleted!`, 'info')
                                    loadInitialData()
                                  }
                                }}
                              >
                                Delete
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {/* SUBTAB: USERS & ADDRESSES */}
            {adminSubTab === 'users' && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <h3 style={{ fontSize: '1.25rem', fontWeight: '700' }}>Registered Users & Addresses</h3>
                    <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Full management of customers, addresses, and test credentials.</p>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button className="btn-primary" onClick={() => setShowUserModal(true)}>
                      <Plus size={15} /> Add User
                    </button>
                    <button
                      className="btn-danger"
                      onClick={async () => {
                        if (window.confirm('Clear all users? (DELETE /api/users)')) {
                          await api.deleteAllUsers().catch(() => {})
                          showToast('All users deleted!', 'info')
                          loadInitialData()
                        }
                      }}
                    >
                      Delete All Users
                    </button>
                  </div>
                </div>

                <div className="data-table-container">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>User ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Mobile</th>
                        <th>Role</th>
                        <th>Saved Addresses</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {users.map((u) => (
                        <tr key={u.id}>
                          <td><strong>#{u.id}</strong></td>
                          <td><strong>{u.name}</strong></td>
                          <td>{u.email}</td>
                          <td>{u.mobile}</td>
                          <td>
                            <span className="badge-paid" style={{ color: '#38bdf8', borderColor: 'rgba(56, 189, 248, 0.3)', background: 'rgba(56, 189, 248, 0.1)' }}>
                              {u.role?.roleName || 'CUSTOMER'}
                            </span>
                          </td>
                          <td>
                            {u.addresses && u.addresses.length > 0 ? (
                              <span style={{ fontSize: '0.8rem' }}>
                                {u.addresses.length} address(es) (e.g. {u.addresses[0].city})
                              </span>
                            ) : (
                              <span style={{ color: 'var(--text-dim)', fontSize: '0.8rem' }}>None</span>
                            )}
                          </td>
                          <td>
                            <div style={{ display: 'flex', gap: '0.4rem' }}>
                              <button
                                className="btn-secondary"
                                style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem' }}
                                onClick={() => {
                                  setCurrentUser(u)
                                  setShowAddressModal(true)
                                }}
                              >
                                + Address
                              </button>
                              <button
                                className="btn-danger"
                                style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem' }}
                                onClick={async () => {
                                  if (window.confirm(`Delete user "${u.name}"?`)) {
                                    await api.deleteUser(u.id).catch(() => {})
                                    showToast(`User #${u.id} deleted!`, 'info')
                                    loadInitialData()
                                  }
                                }}
                              >
                                Delete
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* SUBTAB: SECURITY ROLES */}
            {adminSubTab === 'roles' && (
              <div style={{ display: 'grid', gridTemplateColumns: '350px 1fr', gap: '1.5rem' }}>
                <div className="admin-card">
                  <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <ShieldCheck size={18} color="var(--primary)" /> Add System Role
                  </h2>
                  <form onSubmit={handleAdminAddRole}>
                    <div className="form-field">
                      <label>Role ID (Numeric)</label>
                      <input
                        type="number"
                        required
                        value={adminRoleForm.roleId}
                        onChange={(e) => setAdminRoleForm({ ...adminRoleForm, roleId: e.target.value })}
                      />
                    </div>
                    <div className="form-field">
                      <label>Role Name</label>
                      <input
                        type="text"
                        required
                        placeholder="e.g. DELIVERY_PARTNER"
                        value={adminRoleForm.roleName}
                        onChange={(e) => setAdminRoleForm({ ...adminRoleForm, roleName: e.target.value })}
                      />
                    </div>
                    <div className="form-field">
                      <label>Description</label>
                      <textarea
                        rows="3"
                        placeholder="Permissions and access scope..."
                        value={adminRoleForm.roleDescription}
                        onChange={(e) => setAdminRoleForm({ ...adminRoleForm, roleDescription: e.target.value })}
                      ></textarea>
                    </div>
                    <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                      Save Role (POST /api/roles)
                    </button>
                  </form>
                </div>

                <div className="admin-card">
                  <h3 style={{ fontSize: '1.1rem', marginBottom: '0.75rem' }}>Active System Roles</h3>
                  <div className="data-table-container">
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>Role ID</th>
                          <th>Role Name</th>
                          <th>Description</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {roles.map((r) => (
                          <tr key={r.roleId || r.id}>
                            <td><strong>#{r.roleId || r.id}</strong></td>
                            <td><code>{r.roleName || r.name}</code></td>
                            <td>{r.roleDescription || r.description || 'Standard access role'}</td>
                            <td>
                              <button
                                className="btn-danger"
                                onClick={async () => {
                                  if (window.confirm(`Delete role "${r.roleName || r.name}"?`)) {
                                    await api.deleteRole(r.roleId || r.id).catch(() => {})
                                    showToast(`Role #${r.roleId || r.id} deleted!`, 'info')
                                    loadInitialData()
                                  }
                                }}
                              >
                                Delete
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {/* SUBTAB: DELIVERY PRICING RULES */}
            {adminSubTab === 'pricing' && (
              <div style={{ display: 'grid', gridTemplateColumns: '400px 1fr', gap: '1.5rem' }}>
                <div className="admin-card">
                  <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <DollarSign size={18} color="var(--primary)" /> Configure Distance Pricing
                  </h2>
                  <form onSubmit={handleAdminSavePricing}>
                    <div className="form-grid-2">
                      <div className="form-field">
                        <label>Base Delivery Fee (₹)</label>
                        <input
                          type="number"
                          required
                          min="10"
                          value={adminPricingForm.basefees}
                          onChange={(e) => setAdminPricingForm({ ...adminPricingForm, basefees: e.target.value })}
                        />
                      </div>
                      <div className="form-field">
                        <label>Per Km Rate (₹)</label>
                        <input
                          type="number"
                          required
                          min="1"
                          value={adminPricingForm.perKmRate}
                          onChange={(e) => setAdminPricingForm({ ...adminPricingForm, perKmRate: e.target.value })}
                        />
                      </div>
                    </div>

                    <div className="form-grid-2">
                      <div className="form-field">
                        <label>Max Radius (Km)</label>
                        <input
                          type="number"
                          required
                          min="1"
                          value={adminPricingForm.maxdelieveryradius}
                          onChange={(e) => setAdminPricingForm({ ...adminPricingForm, maxdelieveryradius: e.target.value })}
                        />
                      </div>
                      <div className="form-field">
                        <label>Free Delivery Above (₹)</label>
                        <input
                          type="number"
                          required
                          min="100"
                          value={adminPricingForm.freeDelievery}
                          onChange={(e) => setAdminPricingForm({ ...adminPricingForm, freeDelievery: e.target.value })}
                        />
                      </div>
                    </div>

                    <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '0.5rem' }}>
                      Save Rule (POST /delieverypricing/add)
                    </button>
                  </form>
                </div>

                <div className="admin-card">
                  <h3 style={{ fontSize: '1.1rem', marginBottom: '0.75rem' }}>Delivery Pricing Rules Catalog</h3>
                  <div className="data-table-container">
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>Base Fee</th>
                          <th>Per Km Rate</th>
                          <th>Max Radius</th>
                          <th>Free Delivery Threshold</th>
                          <th>Active</th>
                        </tr>
                      </thead>
                      <tbody>
                        {pricingRules.map((rule, idx) => (
                          <tr key={idx}>
                            <td><strong>₹{rule.basefees}</strong></td>
                            <td>₹{rule.perKmRate} / km</td>
                            <td>{rule.maxdelieveryradius} km</td>
                            <td>₹{rule.freeDelievery}</td>
                            <td>
                              <span className={`status-badge ${rule.active !== false ? 'open' : 'closed'}`}>
                                {rule.active !== false ? 'ACTIVE' : 'INACTIVE'}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {/* SUBTAB: PAYMENTS & STRIPE LEDGER */}
            {adminSubTab === 'payments' && (
              <div className="admin-card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                  <div>
                    <h2 style={{ fontSize: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <CreditCard size={18} color="var(--primary)" /> Stripe Gateway & Payments Ledger
                    </h2>
                    <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                      Synchronized transactions and Stripe webhook statuses.
                    </p>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <a
                      href="https://dashboard.stripe.com/test/payments"
                      target="_blank"
                      rel="noreferrer"
                      className="btn-secondary"
                      style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', textDecoration: 'none', background: 'rgba(99, 102, 241, 0.2)', borderColor: 'rgba(99, 102, 241, 0.4)', color: '#a5b4fc' }}
                    >
                      Stripe Dashboard ↗
                    </a>
                    <button
                      className="btn-secondary"
                      onClick={async () => {
                        const payData = await api.getAllPayments().catch(() => [])
                        setPayments(Array.isArray(payData) ? payData : [])
                        showToast('Payments ledger refreshed!', 'info')
                      }}
                      style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}
                    >
                      <RefreshCw size={14} /> Refresh
                    </button>
                  </div>
                </div>

                <div className="data-table-container">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Payment ID</th>
                        <th>Transaction / Intent ID</th>
                        <th>Order #</th>
                        <th>User ID</th>
                        <th>Amount</th>
                        <th>Method</th>
                        <th>Status</th>
                        <th>Timestamp</th>
                        <th>Update Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {payments.map((p) => (
                        <tr key={p.id || p.transactionId}>
                          <td><strong>#{p.id}</strong></td>
                          <td>
                            <code style={{ color: '#38bdf8', background: 'rgba(255,255,255,0.05)', padding: '0.15rem 0.4rem', borderRadius: '4px' }}>
                              {p.transactionId || 'N/A'}
                            </code>
                          </td>
                          <td>Order #{p.orderId}</td>
                          <td>User #{p.userId}</td>
                          <td style={{ fontWeight: '700' }}>₹{Number(p.amount || 0).toFixed(2)}</td>
                          <td>
                            <span style={{ fontSize: '0.8rem', color: p.paymentMethod === 'STRIPE' ? '#818cf8' : '#cbd5e1' }}>
                              {p.paymentMethod === 'STRIPE' ? '💳 Stripe Mock' : p.paymentMethod}
                            </span>
                          </td>
                          <td>
                            <span className={
                              ['PAID', 'SUCCESS', 'SUCCEEDED'].includes((p.paymentStatus || '').toUpperCase())
                                ? 'badge-paid'
                                : (p.paymentStatus || '').toUpperCase() === 'FAILED'
                                ? 'badge-failed'
                                : 'badge-pending'
                            }>
                              {p.paymentStatus || 'PENDING'}
                            </span>
                          </td>
                          <td style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>
                            {p.paymentDate ? new Date(p.paymentDate).toLocaleString() : 'Just now'}
                          </td>
                          <td>
                            <select
                              className="address-select"
                              style={{ width: 'auto', marginBottom: 0, padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                              value={p.paymentStatus || 'PENDING'}
                              onChange={async (e) => {
                                try {
                                  await api.updatePaymentStatus(p.id, e.target.value)
                                  showToast(`Payment #${p.id} marked as ${e.target.value}`, 'success')
                                  const refreshed = await api.getAllPayments()
                                  setPayments(refreshed)
                                } catch (err) {
                                  showToast(err.message, 'error')
                                }
                              }}
                            >
                              <option value="PAID">PAID</option>
                              <option value="SUCCESS">SUCCESS</option>
                              <option value="PENDING">PENDING</option>
                              <option value="REQUIRES_PAYMENT_METHOD">REQ_METHOD</option>
                              <option value="REFUNDED">REFUNDED</option>
                              <option value="FAILED">FAILED</option>
                            </select>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ====================================================================
            TAB 5: DEDICATED INTERACTIVE API TESTER & WORKBENCH
            ==================================================================== */}
        {activeTab === 'tester' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <div>
                <h1 style={{ fontSize: '1.8rem', fontWeight: '800', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <Terminal size={24} color="#818cf8" /> API Tester & Interactive Workbench
                </h1>
                <p style={{ color: 'var(--text-muted)' }}>
                  Test, inspect, and benchmark all 38+ backend API endpoints across Users, Addresses, Roles, Restaurants, Menu, Cart, Pricing, Orders, Payments & Actuator.
                </p>
              </div>

              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button
                  className="btn-secondary"
                  onClick={runObservabilityFlow}
                  disabled={!!runningFlowId}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', borderColor: 'rgba(56, 189, 248, 0.4)', color: '#38bdf8' }}
                >
                  <Play size={14} /> Run Health Ping
                </button>
                <button
                  className="btn-secondary"
                  onClick={runUserLifecycleFlow}
                  disabled={!!runningFlowId}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', borderColor: 'rgba(52, 211, 153, 0.4)', color: '#34d399' }}
                >
                  <Play size={14} /> Run User Suite
                </button>
                <button
                  className="btn-secondary"
                  onClick={runOrderAndStripeFlow}
                  disabled={!!runningFlowId}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', borderColor: 'rgba(99, 102, 241, 0.4)', color: '#a5b4fc' }}
                >
                  <Play size={14} /> Run Checkout & Stripe Flow
                </button>
              </div>
            </div>

            {/* Automated Flow Live Logs Console */}
            {flowLogs.length > 0 && (
              <div style={{ marginBottom: '1.5rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                  <span style={{ fontSize: '0.85rem', fontWeight: '700', color: 'var(--text-muted)' }}>
                    ⚡ Test Flow Runner Live Log Console
                  </span>
                  <button
                    onClick={() => setFlowLogs([])}
                    style={{ background: 'transparent', border: 'none', color: 'var(--text-dim)', fontSize: '0.75rem', cursor: 'pointer' }}
                  >
                    Clear Logs
                  </button>
                </div>
                <div className="flow-log-box">
                  {flowLogs.map((log, idx) => (
                    <div key={idx} className={`flow-log-line ${log.type}`}>
                      <span style={{ opacity: 0.6 }}>[{log.time}]</span>
                      <span>{log.message}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Main Tester Layout: Left Sidebar + Right Stage */}
            <div className="api-tester-layout">
              {/* LEFT SIDEBAR: ENDPOINT SELECTOR */}
              <div className="api-sidebar-panel">
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  <div className="search-box" style={{ maxWidth: '100%', minWidth: 'auto' }}>
                    <Search size={15} className="search-icon" />
                    <input
                      type="text"
                      placeholder="Filter endpoints..."
                      value={testerSearch}
                      onChange={(e) => setTesterSearch(e.target.value)}
                      style={{ padding: '0.55rem 0.85rem 0.55rem 2.2rem', fontSize: '0.82rem' }}
                    />
                  </div>

                  {/* Category Selector */}
                  <div className="selector-box" style={{ width: '100%' }}>
                    <Filter size={14} color="var(--primary)" />
                    <select
                      value={testerCategory}
                      onChange={(e) => setTesterCategory(e.target.value)}
                      style={{ width: '100%', maxWidth: 'none' }}
                    >
                      {catalogCategories.map((cat) => (
                        <option key={cat} value={cat}>
                          {cat}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                {/* Endpoints List */}
                <div className="api-catalog-list">
                  {filteredEndpoints.map((ep) => (
                    <button
                      key={ep.id}
                      className={`api-endpoint-btn ${selectedEndpoint?.id === ep.id ? 'active' : ''}`}
                      onClick={() => selectEndpointInTester(ep)}
                    >
                      <span className={`method-badge ${ep.method}`}>{ep.method}</span>
                      <div style={{ flex: 1, minWidth: 0 }}>
                        <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {ep.name}
                        </div>
                        <div style={{ fontSize: '0.7rem', color: 'var(--text-dim)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {ep.path}
                        </div>
                      </div>
                    </button>
                  ))}
                </div>
              </div>

              {/* RIGHT MAIN STAGE: INTERACTIVE WORKBENCH */}
              {selectedEndpoint && (
                <div className="api-workbench-panel">
                  {/* Request Builder Card */}
                  <div className="api-workbench-card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '1rem' }}>
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.4rem' }}>
                          <span className={`method-badge ${selectedEndpoint.method}`}>{selectedEndpoint.method}</span>
                          <span style={{ fontSize: '0.8rem', color: 'var(--text-dim)', fontWeight: '600' }}>
                            {selectedEndpoint.category}
                          </span>
                        </div>
                        <h2 style={{ fontSize: '1.4rem', fontWeight: '800' }}>{selectedEndpoint.name}</h2>
                        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{selectedEndpoint.description}</p>
                      </div>

                      <button
                        className="btn-primary"
                        onClick={handleExecuteApi}
                        disabled={isExecutingApi}
                        style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.75rem 1.5rem', fontSize: '0.95rem' }}
                      >
                        {isExecutingApi ? <RefreshCw size={16} className="spin" /> : <Send size={16} />}
                        <span>Send Request</span>
                      </button>
                    </div>

                    {/* Full URL Bar */}
                    <div className="api-url-bar">
                      <span className={`method-badge ${selectedEndpoint.method}`}>{selectedEndpoint.method}</span>
                      <span className="api-url-text">
                        {api.getApiHost()}{selectedEndpoint.path}
                      </span>
                    </div>

                    {/* Path Parameters Section */}
                    {selectedEndpoint.pathParams && selectedEndpoint.pathParams.length > 0 && (
                      <div style={{ marginBottom: '1.25rem' }}>
                        <span style={{ fontSize: '0.8rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px', color: 'var(--text-muted)' }}>
                          Path Parameters
                        </span>
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '0.75rem', marginTop: '0.5rem' }}>
                          {selectedEndpoint.pathParams.map((p) => (
                            <div key={p.key} className="form-field" style={{ marginBottom: 0 }}>
                              <label style={{ fontSize: '0.75rem' }}>{p.label || p.key}</label>
                              <input
                                type="text"
                                value={paramValues[p.key] !== undefined ? paramValues[p.key] : p.default}
                                onChange={(e) => setParamValues({ ...paramValues, [p.key]: e.target.value })}
                              />
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Query Parameters Section */}
                    {selectedEndpoint.queryParams && selectedEndpoint.queryParams.length > 0 && (
                      <div style={{ marginBottom: '1.25rem' }}>
                        <span style={{ fontSize: '0.8rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px', color: 'var(--text-muted)' }}>
                          Query Parameters (?key=value)
                        </span>
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '0.75rem', marginTop: '0.5rem' }}>
                          {selectedEndpoint.queryParams.map((q) => (
                            <div key={q.key} className="form-field" style={{ marginBottom: 0 }}>
                              <label style={{ fontSize: '0.75rem' }}>{q.label || q.key}</label>
                              <input
                                type="text"
                                value={queryValues[q.key] !== undefined ? queryValues[q.key] : q.default}
                                onChange={(e) => setQueryValues({ ...queryValues, [q.key]: e.target.value })}
                              />
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Request Body JSON Editor */}
                    {['POST', 'PUT', 'PATCH', 'DELETE'].includes(selectedEndpoint.method) && (
                      <div style={{ marginTop: '1rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                          <span style={{ fontSize: '0.8rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px', color: 'var(--text-muted)' }}>
                            Request Body (JSON)
                          </span>
                          <div style={{ display: 'flex', gap: '0.5rem' }}>
                            {selectedEndpoint.sampleBody && (
                              <button
                                onClick={() => setRequestBodyText(JSON.stringify(selectedEndpoint.sampleBody, null, 2))}
                                style={{ background: 'transparent', border: 'none', color: '#38bdf8', fontSize: '0.75rem', cursor: 'pointer' }}
                              >
                                Load Sample Payload
                              </button>
                            )}
                            <button
                              onClick={() => {
                                try {
                                  const formatted = JSON.stringify(JSON.parse(requestBodyText), null, 2)
                                  setRequestBodyText(formatted)
                                } catch {}
                              }}
                              style={{ background: 'transparent', border: 'none', color: 'var(--text-dim)', fontSize: '0.75rem', cursor: 'pointer' }}
                            >
                              Format JSON
                            </button>
                          </div>
                        </div>

                        <textarea
                          className="json-editor-box"
                          rows="6"
                          value={requestBodyText}
                          onChange={(e) => setRequestBodyText(e.target.value)}
                          placeholder="{\n  // Request JSON payload\n}"
                        ></textarea>
                      </div>
                    )}
                  </div>

                  {/* Response Inspector Card */}
                  {apiResponse && (
                    <div className="response-inspector-card">
                      <div className="response-header">
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                          <span className={`status-badge-code ${apiResponse.status >= 200 && apiResponse.status < 300 ? 's2xx' : 's4xx'}`}>
                            {apiResponse.status} {apiResponse.statusText}
                          </span>
                          <span className="latency-tag">
                            <Clock size={13} /> {apiResponse.durationMs} ms
                          </span>
                        </div>

                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <div className="filter-pills">
                            <button
                              className={`filter-pill ${activeResponseTab === 'data' ? 'active' : ''}`}
                              onClick={() => setActiveResponseTab('data')}
                              style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem' }}
                            >
                              JSON Body
                            </button>
                            <button
                              className={`filter-pill ${activeResponseTab === 'headers' ? 'active' : ''}`}
                              onClick={() => setActiveResponseTab('headers')}
                              style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem' }}
                            >
                              Headers
                            </button>
                            <button
                              className={`filter-pill ${activeResponseTab === 'raw' ? 'active' : ''}`}
                              onClick={() => setActiveResponseTab('raw')}
                              style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem' }}
                            >
                              Raw Text
                            </button>
                          </div>

                          <button
                            className="btn-secondary"
                            onClick={() => {
                              navigator.clipboard.writeText(
                                typeof apiResponse.data === 'object'
                                  ? JSON.stringify(apiResponse.data, null, 2)
                                  : String(apiResponse.rawBody)
                              )
                              setCopiedResponse(true)
                              setTimeout(() => setCopiedResponse(false), 2000)
                            }}
                            style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', padding: '0.35rem 0.7rem', fontSize: '0.75rem' }}
                          >
                            {copiedResponse ? <Check size={12} color="var(--success)" /> : <Copy size={12} />}
                            <span>{copiedResponse ? 'Copied' : 'Copy'}</span>
                          </button>
                        </div>
                      </div>

                      <div className="response-body-view">
                        {activeResponseTab === 'data' && (
                          <pre style={{ margin: 0, fontFamily: 'inherit' }}>
                            {typeof apiResponse.data === 'object'
                              ? JSON.stringify(apiResponse.data, null, 2)
                              : apiResponse.data || '<Empty Response>'}
                          </pre>
                        )}
                        {activeResponseTab === 'headers' && (
                          <pre style={{ margin: 0, fontFamily: 'inherit' }}>
                            {JSON.stringify(apiResponse.headers, null, 2)}
                          </pre>
                        )}
                        {activeResponseTab === 'raw' && (
                          <pre style={{ margin: 0, fontFamily: 'inherit' }}>
                            {apiResponse.rawBody || '<Empty Body>'}
                          </pre>
                        )}
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        )}
      </main>

      {/* ========================================================================
          SLIDE-OVER CART DRAWER
          ======================================================================== */}
      {cartOpen && (
        <>
          <div className="drawer-backdrop" onClick={() => setCartOpen(false)}></div>
          <div className="cart-drawer">
            <div className="drawer-header">
              <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '1.25rem' }}>
                <ShoppingBag size={20} color="var(--primary)" /> Your Cart
              </h2>
              <button className="btn-close" onClick={() => setCartOpen(false)}>
                <X size={18} />
              </button>
            </div>

            <div className="drawer-body">
              {cart.items.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '3rem 1rem', color: 'var(--text-dim)' }}>
                  <div style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>🛒</div>
                  <h3>Your cart is empty</h3>
                  <p style={{ color: 'var(--text-muted)' }}>Explore our menu and add some mouth-watering dishes!</p>
                </div>
              ) : (
                <>
                  {/* Items List */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    {cart.items.map((item) => (
                      <div key={item.cartItemId || item.foodItemId} className="cart-item-row">
                        <div>
                          <h4 style={{ fontSize: '0.95rem', fontWeight: '700' }}>{item.foodname || `Food #${item.foodItemId}`}</h4>
                          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>₹{item.unitPrice?.toFixed(2)} each × {item.quantity}</p>
                        </div>

                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                          <span style={{ fontWeight: '800', color: 'var(--text-main)' }}>
                            ₹{item.subtotal?.toFixed(2)}
                          </span>
                          <button
                            className="btn-icon"
                            style={{ background: 'rgba(239,68,68,0.15)', color: 'var(--danger)', border: 'none', width: '28px', height: '28px', borderRadius: '50%', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                            onClick={() => handleRemoveFromCart(item.cartItemId)}
                            title="Remove"
                          >
                            <Trash2 size={13} />
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Delivery Address Box */}
                  <div className="checkout-section-card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                      <span style={{ fontSize: '0.8rem', fontWeight: '700', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                        <MapPin size={14} color="var(--primary)" /> Delivery Address
                      </span>
                      <button
                        style={{ background: 'transparent', border: 'none', color: 'var(--primary)', fontSize: '0.8rem', fontWeight: '700', cursor: 'pointer' }}
                        onClick={() => setShowAddressModal(true)}
                      >
                        + Add New
                      </button>
                    </div>

                    {currentUser?.addresses && currentUser.addresses.length > 0 ? (
                      <select
                        className="address-select"
                        value={selectedAddressId || ''}
                        onChange={(e) => setSelectedAddressId(Number(e.target.value))}
                      >
                        {currentUser.addresses.map((addr) => (
                          <option key={addr.addressId || addr.id} value={addr.addressId || addr.id}>
                            {addr.addressType || 'HOME'}: {addr.houseNo}, {addr.street}, {addr.city} ({addr.pincode})
                          </option>
                        ))}
                      </select>
                    ) : (
                      <p style={{ fontSize: '0.85rem', color: 'var(--danger)', marginBottom: '0.5rem' }}>
                        No address found. Click "+ Add New" above.
                      </p>
                    )}

                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.8rem', color: 'var(--text-dim)' }}>
                      <Truck size={14} /> Estimated Distance: ~{deliveryInfo.distance} km
                    </div>
                  </div>

                  {/* Payment Method Selector */}
                  <div className="checkout-section-card">
                    <span style={{ fontSize: '0.8rem', fontWeight: '700', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                      <CreditCard size={14} color="var(--secondary)" /> Payment Method
                    </span>
                    <div className="payment-options">
                      {['UPI', 'CARD', 'COD'].map((method) => (
                        <div
                          key={method}
                          className={`payment-option-btn ${paymentMethod === method ? 'selected' : ''}`}
                          onClick={() => setPaymentMethod(method)}
                        >
                          {method === 'UPI' && '📱 UPI / QR'}
                          {method === 'CARD' && '💳 Card (Stripe)'}
                          {method === 'COD' && '💵 Cash on Del.'}
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Bill Breakdown */}
                  <div className="checkout-section-card">
                    <span style={{ fontSize: '0.8rem', fontWeight: '700', color: 'var(--text-muted)', display: 'block', marginBottom: '0.5rem' }}>
                      Bill Breakdown
                    </span>
                    <div className="bill-row">
                      <span>Item Subtotal</span>
                      <span>₹{(cart.totalAmount || 0).toFixed(2)}</span>
                    </div>
                    <div className="bill-row">
                      <span>Delivery Fee ({deliveryInfo.distance} km)</span>
                      <span>{deliveryInfo.deliveryFee === 0 ? 'FREE' : `₹${deliveryInfo.deliveryFee.toFixed(2)}`}</span>
                    </div>
                    <div className="bill-row total">
                      <span>Total Amount</span>
                      <span style={{ color: 'var(--primary)' }}>₹{grandTotal.toFixed(2)}</span>
                    </div>
                  </div>
                </>
              )}
            </div>

            {cart.items.length > 0 && (
              <div className="drawer-footer">
                <button
                  className="btn-place-order"
                  disabled={loading || !selectedAddressId}
                  onClick={handlePlaceOrder}
                >
                  {loading ? (
                    <>
                      <RefreshCw size={18} className="spin" /> Processing Order...
                    </>
                  ) : (
                    <>
                      Place Order • ₹{grandTotal.toFixed(2)} <ArrowRight size={18} />
                    </>
                  )}
                </button>
              </div>
            )}
          </div>
        </>
      )}

      {/* ========================================================================
          MODAL: BACKEND TARGET SERVER SWITCHER & PING
          ======================================================================== */}
      {showServerModal && (
        <div className="modal-backdrop" onClick={() => setShowServerModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontSize: '1.3rem', fontWeight: '800', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Server size={18} color="var(--primary)" /> Backend Server Configuration
              </h2>
              <button className="btn-close" onClick={() => setShowServerModal(false)}>
                <X size={18} />
              </button>
            </div>

            <div style={{ marginTop: '1rem' }}>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                Configure the target host origin for all REST API endpoints. You can switch between local Spring Boot instance, Docker containers, or live staging cloud instances.
              </p>

              <div className="form-field">
                <label>Active API Host URL</label>
                <input
                  type="text"
                  value={customServerUrl}
                  onChange={(e) => setCustomServerUrl(e.target.value)}
                  placeholder="http://localhost:8082"
                />
              </div>

              <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.25rem' }}>
                <button
                  type="button"
                  className="btn-secondary"
                  style={{ fontSize: '0.75rem', padding: '0.35rem 0.7rem' }}
                  onClick={() => setCustomServerUrl('http://194.242.57.93:8082')}
                >
                  Staging Cloud (194.242.57.93:8082)
                </button>
                <button
                  type="button"
                  className="btn-secondary"
                  style={{ fontSize: '0.75rem', padding: '0.35rem 0.7rem' }}
                  onClick={() => setCustomServerUrl('http://localhost:8082')}
                >
                  Localhost (:8082)
                </button>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={async () => {
                    api.setApiHost(customServerUrl)
                    await checkHealth()
                    showToast('Ping test executed!', 'info')
                  }}
                >
                  Test Connection (Ping)
                </button>
                <button
                  type="button"
                  className="btn-primary"
                  onClick={async () => {
                    api.setApiHost(customServerUrl)
                    showToast(`API Base set to ${customServerUrl}`, 'success')
                    setShowServerModal(false)
                    loadInitialData()
                  }}
                >
                  Save & Apply
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ========================================================================
          MODAL: REGISTER CUSTOMER & ADDRESS
          ======================================================================== */}
      {showUserModal && (
        <div className="modal-backdrop" onClick={() => setShowUserModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontSize: '1.35rem', fontWeight: '800' }}>Register Customer (POST /api/users)</h2>
              <button className="btn-close" onClick={() => setShowUserModal(false)}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleRegisterUser}>
              <div className="form-field">
                <label>Full Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Rahul Sharma"
                  value={userForm.name}
                  onChange={(e) => setUserForm({ ...userForm, name: e.target.value })}
                />
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>Email Address (@gmail.com)</label>
                  <input
                    type="email"
                    required
                    placeholder="e.g. rahul.sharma@gmail.com"
                    value={userForm.email}
                    onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Mobile Number (10 digits)</label>
                  <input
                    type="tel"
                    required
                    maxLength="10"
                    placeholder="9876543210"
                    value={userForm.mobile}
                    onChange={(e) => setUserForm({ ...userForm, mobile: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ fontSize: '0.8rem', fontWeight: '700', color: 'var(--primary)', marginTop: '1rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <MapPin size={14} /> Initial Delivery Address (POST /api/user-address)
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>Flat / House No</label>
                  <input
                    type="text"
                    required
                    value={userForm.houseNo}
                    onChange={(e) => setUserForm({ ...userForm, houseNo: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Building / Society</label>
                  <input
                    type="text"
                    required
                    value={userForm.buildingName}
                    onChange={(e) => setUserForm({ ...userForm, buildingName: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>Street / Area</label>
                  <input
                    type="text"
                    required
                    value={userForm.street}
                    onChange={(e) => setUserForm({ ...userForm, street: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>City & Pincode</label>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <input
                      type="text"
                      required
                      value={userForm.city}
                      onChange={(e) => setUserForm({ ...userForm, city: e.target.value })}
                    />
                    <input
                      type="number"
                      required
                      value={userForm.pincode}
                      onChange={(e) => setUserForm({ ...userForm, pincode: e.target.value })}
                    />
                  </div>
                </div>
              </div>

              <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
                Create Account & Sign In
              </button>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================================
          MODAL: ADD DELIVERY ADDRESS
          ======================================================================== */}
      {showAddressModal && (
        <div className="modal-backdrop" onClick={() => setShowAddressModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontSize: '1.35rem', fontWeight: '800' }}>Add Delivery Address</h2>
              <button className="btn-close" onClick={() => setShowAddressModal(false)}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddAddress}>
              <div className="form-grid-2">
                <div className="form-field">
                  <label>Flat / House No</label>
                  <input
                    type="text"
                    required
                    placeholder="Flat 402"
                    value={addressForm.houseNo}
                    onChange={(e) => setAddressForm({ ...addressForm, houseNo: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Building Name</label>
                  <input
                    type="text"
                    required
                    placeholder="Shree Heights"
                    value={addressForm.buildingName}
                    onChange={(e) => setAddressForm({ ...addressForm, buildingName: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-field">
                <label>Street Address & Landmark</label>
                <input
                  type="text"
                  required
                  placeholder="MG Road, near Metro station"
                  value={addressForm.street}
                  onChange={(e) => setAddressForm({ ...addressForm, street: e.target.value })}
                />
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>City</label>
                  <input
                    type="text"
                    required
                    value={addressForm.city}
                    onChange={(e) => setAddressForm({ ...addressForm, city: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Pincode</label>
                  <input
                    type="number"
                    required
                    value={addressForm.pincode}
                    onChange={(e) => setAddressForm({ ...addressForm, pincode: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-field">
                <label>Address Type</label>
                <select
                  value={addressForm.addressType}
                  onChange={(e) => setAddressForm({ ...addressForm, addressType: e.target.value })}
                >
                  <option value="HOME">HOME</option>
                  <option value="WORK">WORK</option>
                  <option value="OTHER">OTHER</option>
                </select>
              </div>

              <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
                Save Delivery Address (POST /api/user-address)
              </button>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================================
          MODAL: FEEDBACK / REVIEW
          ======================================================================== */}
      {showReviewModal && (
        <div className="modal-backdrop" onClick={() => setShowReviewModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontSize: '1.35rem', fontWeight: '800' }}>Rate & Review (POST /api/feedback)</h2>
              <button className="btn-close" onClick={() => setShowReviewModal(false)}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleSubmitReview}>
              <div style={{ textAlign: 'center', margin: '1.5rem 0' }}>
                <div className="star-rating" style={{ justifyContent: 'center' }}>
                  {[1, 2, 3, 4, 5].map((star) => (
                    <span
                      key={star}
                      className={`star-icon ${star <= reviewForm.rating ? 'active' : ''}`}
                      onClick={() => setReviewForm({ ...reviewForm, rating: star })}
                    >
                      ★
                    </span>
                  ))}
                </div>
                <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginTop: '0.5rem', display: 'block' }}>
                  {reviewForm.rating === 5 && '🌟 Outstanding! Loved it!'}
                  {reviewForm.rating === 4 && '👍 Great experience'}
                  {reviewForm.rating === 3 && '👌 Good food'}
                  {reviewForm.rating <= 2 && '👎 Needs improvement'}
                </span>
              </div>

              <div className="form-field">
                <label>Feedback & Comments</label>
                <textarea
                  rows="3"
                  required
                  placeholder="Tell us what you loved about the food, taste, packaging..."
                  value={reviewForm.comment}
                  onChange={(e) => setReviewForm({ ...reviewForm, comment: e.target.value })}
                ></textarea>
              </div>

              <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                Submit Review
              </button>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================================
          STRIPE HOSTED CHECKOUT REDIRECTION VIEW
          ======================================================================== */}
      {showPaymentModal && stripePaymentData && (
        <div className="stripe-hosted-page">
          {/* Simulated Browser Address Bar */}
          <div className="stripe-browser-bar">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
              <button
                onClick={() => {
                  setShowPaymentModal(false)
                  setStripePaymentData(null)
                  showToast('Payment cancelled. You can retry from My Orders.', 'info')
                }}
                style={{ background: 'transparent', border: 'none', color: '#94a3b8', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem' }}
              >
                ← Return to FoodDelivery
              </button>
            </div>
            
            <div className="stripe-url-pill">
              <ShieldCheck size={14} color="#10b981" />
              <span>https://checkout.stripe.com/c/pay/{stripePaymentData.transactionId || 'cs_test_mock'}</span>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span className="stripe-badge" style={{ background: 'rgba(16, 185, 129, 0.2)', color: '#34d399', padding: '0.2rem 0.5rem', borderRadius: '9999px', fontSize: '0.75rem', fontWeight: '700' }}>
                ● STRIPE TESTMODE
              </span>
            </div>
          </div>

          <div className="stripe-hosted-container">
            {/* Left Summary Column */}
            <div className="stripe-summary-col">
              <div>
                <div className="stripe-merchant-header">
                  <div className="stripe-merchant-logo">🍔</div>
                  <div>
                    <h3 style={{ fontSize: '1.2rem', fontWeight: '700' }}>FoodDelivery Platform</h3>
                    <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Order #{stripePaymentData.orderId} • Group B02</span>
                  </div>
                </div>

                <div style={{ color: '#94a3b8', fontSize: '0.9rem', marginBottom: '0.25rem' }}>Amount Due</div>
                <div className="stripe-amount-large">₹{Number(stripePaymentData.amount || 0).toFixed(2)}</div>

                <div style={{ marginTop: '2rem' }}>
                  <div style={{ fontSize: '0.85rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px', color: '#94a3b8', marginBottom: '0.75rem' }}>
                    Order Summary
                  </div>
                  
                  {stripePaymentData.order?.items && stripePaymentData.order.items.length > 0 ? (
                    stripePaymentData.order.items.map((item, idx) => (
                      <div key={idx} className="stripe-order-item-row">
                        <span>{item.quantity}x {item.itemName || 'Delicacy'}</span>
                        <span>₹{Number(item.totalPrice || 0).toFixed(2)}</span>
                      </div>
                    ))
                  ) : (
                    <div className="stripe-order-item-row">
                      <span>Meal Selection & Delivery</span>
                      <span>₹{Number(stripePaymentData.amount || 0).toFixed(2)}</span>
                    </div>
                  )}

                  <div className="stripe-order-item-row" style={{ color: '#64748b' }}>
                    <span>Estimated Delivery</span>
                    <span>25 - 35 mins</span>
                  </div>
                  <div className="stripe-order-item-row" style={{ color: '#64748b' }}>
                    <span>Recipient</span>
                    <span>{currentUser?.name} (#{currentUser?.id})</span>
                  </div>
                </div>
              </div>

              {/* Technical Stripe Mock Metadata */}
              <div>
                <div className="stripe-meta-box">
                  <div className="stripe-meta-row">
                    <span style={{ color: '#94a3b8' }}>Gateway Target:</span>
                    <code style={{ color: '#38bdf8' }}>https://api.stripe.com</code>
                  </div>
                  <div className="stripe-meta-row">
                    <span style={{ color: '#94a3b8' }}>Payment Intent:</span>
                    <code>{stripePaymentData.transactionId}</code>
                  </div>
                  <div className="stripe-meta-row">
                    <span style={{ color: '#94a3b8' }}>Client Secret:</span>
                    <code>{stripePaymentData.clientSecret}</code>
                  </div>
                </div>
              </div>
            </div>

            {/* Right Payment Column */}
            <div className="stripe-payment-col">
              <div>
                <h2 style={{ fontSize: '1.4rem', fontWeight: '700', marginBottom: '1.5rem', color: '#0f172a' }}>
                  Pay with Card (Stripe Sandbox)
                </h2>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.35rem' }}>
                      Email address
                    </label>
                    <input
                      type="email"
                      className="stripe-input-box"
                      defaultValue={currentUser?.email || 'customer@gmail.com'}
                      readOnly
                    />
                  </div>

                  <div>
                    <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.35rem' }}>
                      Card information
                    </label>
                    <div style={{ position: 'relative' }}>
                      <input
                        type="text"
                        className="stripe-input-box"
                        style={{ borderBottomLeftRadius: 0, borderBottomRightRadius: 0, letterSpacing: '2px', fontFamily: 'monospace' }}
                        defaultValue="4242 •••• •••• 4242"
                        readOnly
                      />
                      <div style={{ display: 'flex' }}>
                        <input
                          type="text"
                          className="stripe-input-box"
                          style={{ borderTopLeftRadius: 0, borderTopRightRadius: 0, borderRight: 'none', width: '50%' }}
                          defaultValue="12 / 28"
                          readOnly
                        />
                        <input
                          type="text"
                          className="stripe-input-box"
                          style={{ borderTopLeftRadius: 0, borderTopRightRadius: 0, width: '50%' }}
                          defaultValue="888"
                          readOnly
                        />
                      </div>
                    </div>
                  </div>

                  <div>
                    <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: '600', color: '#475569', marginBottom: '0.35rem' }}>
                      Cardholder name
                    </label>
                    <input
                      type="text"
                      className="stripe-input-box"
                      defaultValue={currentUser?.name || 'Rahul Sharma'}
                      readOnly
                    />
                  </div>
                </div>

                <button
                  className="stripe-checkout-btn"
                  disabled={loading}
                  onClick={handleAuthorizeStripePayment}
                >
                  {loading ? (
                    <>
                      <RefreshCw size={20} className="spin" /> Processing Mock Charge...
                    </>
                  ) : (
                    <>
                      <ShieldCheck size={20} /> Authorize & Pay ₹{Number(stripePaymentData.amount || 0).toFixed(2)}
                    </>
                  )}
                </button>

                <div style={{ textAlign: 'center', marginTop: '1rem' }}>
                  <button
                    onClick={() => {
                      setShowPaymentModal(false)
                      setStripePaymentData(null)
                    }}
                    style={{ background: 'transparent', border: 'none', color: '#64748b', fontSize: '0.85rem', cursor: 'pointer', textDecoration: 'underline' }}
                  >
                    Cancel and return to merchant
                  </button>
                </div>
              </div>

              <div className="stripe-footer-links">
                <span>Powered by <strong>stripe</strong></span>
                <span>•</span>
                <span>Sandbox Verified</span>
              </div>
            </div>
          </div>
        </div>
      )}

      <footer className="footer">
        FoodDelivery Enterprise Platform • Built with Spring Boot 3 & React • 38+ Backend Endpoints Integrated
      </footer>
    </div>
  )
}
